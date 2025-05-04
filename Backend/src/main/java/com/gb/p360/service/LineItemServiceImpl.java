package com.gb.p360.service;

import com.gb.p360.data.LineItemDTO;
import com.gb.p360.data.LineItemStatusUpdateDTO;
import com.gb.p360.exception.AccessDeniedException;
import com.gb.p360.exception.InvalidOperationException;
import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.*;
import com.gb.p360.repository.LineItemRepository;
import com.gb.p360.repository.PriceHistoryRepository;
import com.gb.p360.repository.UserRepository;
import com.gb.p360.repository.VendorRepository;
import com.gb.p360.service.interfaces.LineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineItemServiceImpl implements LineItemService {

    private final LineItemRepository lineItemRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public LineItemServiceImpl(
            LineItemRepository lineItemRepository,
            UserRepository userRepository,
            VendorRepository vendorRepository,
            PriceHistoryRepository priceHistoryRepository) {
        this.lineItemRepository = lineItemRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    public LineItem updateLineItemStatus(Long id, LineItemStatusUpdateDTO updateDTO, String username) {
        LineItem lineItem = lineItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line item not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        ProcurementRequest request = lineItem.getProcurementRequest();

        // Request must be submitted
        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new InvalidOperationException("Cannot update line items for non-submitted requests");
        }

        // Check user role and permissions
        if (user.getRole() == Role.PURCHASE_TEAM) {
            LineItemStatus newStatus = LineItemStatus.valueOf(updateDTO.getStatus());

            // Purchase team can set status to Reject, On Hold, Vendor Discussion, Order Placed
            if (newStatus == LineItemStatus.ORDER_RECEIVED) {
                throw new InvalidOperationException("Purchase team cannot mark items as received");
            }

            lineItem.setStatus(newStatus);
            lineItem.setPurchaseTeamRemarks(updateDTO.getRemarks());

            // If changing from ORDER_PLACED back to VENDOR_DISCUSSION, preserve the vendor and pricing info
            if (lineItem.getStatus() == LineItemStatus.ORDER_PLACED && newStatus == LineItemStatus.VENDOR_DISCUSSION) {
                // No additional actions needed, preserve the existing data
            }

        } else if (user.getRole() == Role.FACTORY_USER) {
            // Factory users can only mark items as ORDER_RECEIVED and only if they are ORDER_PLACED
            if (lineItem.getStatus() != LineItemStatus.ORDER_PLACED) {
                throw new InvalidOperationException("Only items with status 'ORDER_PLACED' can be marked as received");
            }

            if (!LineItemStatus.ORDER_RECEIVED.name().equals(updateDTO.getStatus())) {
                throw new InvalidOperationException("Factory users can only mark items as received");
            }

            lineItem.setStatus(LineItemStatus.ORDER_RECEIVED);
            lineItem.setFactoryRemarks(updateDTO.getRemarks());

        } else if (user.getRole() == Role.APPROVAL_TEAM) {
            // Approval team can only reject items
            if (!LineItemStatus.REJECT.name().equals(updateDTO.getStatus())) {
                throw new InvalidOperationException("Approval team can only reject items");
            }

            lineItem.setStatus(LineItemStatus.REJECT);
            lineItem.setPurchaseTeamRemarks(updateDTO.getRemarks()); // Use purchase team remarks field to store approval team comments

        } else {
            throw new AccessDeniedException("User does not have permission to update line item status");
        }

        // Update status changed timestamp
        lineItem.setStatusChangedAt(LocalDateTime.now());

        return lineItemRepository.save(lineItem);
    }

    @Override
    public LineItem updateOrderDetails(Long id, LineItemDTO lineItemDTO, String username) {
        LineItem lineItem = lineItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line item not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only purchase team can update order details
        if (user.getRole() != Role.PURCHASE_TEAM) {
            throw new AccessDeniedException("Only purchase team can update order details");
        }

        // Request must be submitted
        ProcurementRequest request = lineItem.getProcurementRequest();
        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new InvalidOperationException("Cannot update line items for non-submitted requests");
        }

        // Only items in VENDOR_DISCUSSION or ORDER_PLACED status can have details updated
        if (lineItem.getStatus() != LineItemStatus.VENDOR_DISCUSSION &&
                lineItem.getStatus() != LineItemStatus.ORDER_PLACED) {
            throw new InvalidOperationException("Only items in 'VENDOR_DISCUSSION' or 'ORDER_PLACED' status can have details updated");
        }

        // Find or create vendor
        Vendor vendor = null;
        if (lineItemDTO.getVendorName() != null && !lineItemDTO.getVendorName().isEmpty()) {
            Optional<Vendor> existingVendor = vendorRepository.findByName(lineItemDTO.getVendorName());
            if (existingVendor.isPresent()) {
                vendor = existingVendor.get();
            } else {
                vendor = new Vendor();
                vendor.setName(lineItemDTO.getVendorName());
                vendor = vendorRepository.save(vendor);
            }
        }

        // Update line item
        lineItem.setVendor(vendor);
        lineItem.setUnitPrice(lineItemDTO.getUnitPrice());
        lineItem.setTotalOrderValue(lineItemDTO.getTotalOrderValue());
        lineItem.setOrderedQuantity(lineItemDTO.getOrderedQuantity());

        // If status is ORDER_PLACED, create price history entry
        if (lineItem.getStatus() == LineItemStatus.ORDER_PLACED && vendor != null) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setMaterial(lineItem.getMaterial());
            priceHistory.setVendor(vendor);
            priceHistory.setUnitPrice(lineItem.getUnitPrice());
            priceHistory.setOrderDate(LocalDate.now());
            priceHistoryRepository.save(priceHistory);
        }

        return lineItemRepository.save(lineItem);
    }

    @Override
    public LineItem markAsReceived(Long id, String remarks, String username) {
        LineItem lineItem = lineItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line item not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only factory users can mark as received
        if (user.getRole() != Role.FACTORY_USER) {
            throw new AccessDeniedException("Only factory users can mark items as received");
        }

        // Only items with ORDER_PLACED status can be marked as received
        if (lineItem.getStatus() != LineItemStatus.ORDER_PLACED) {
            throw new InvalidOperationException("Only items with status 'ORDER_PLACED' can be marked as received");
        }

        // Update status and remarks
        lineItem.setStatus(LineItemStatus.ORDER_RECEIVED);
        lineItem.setFactoryRemarks(remarks);
        lineItem.setStatusChangedAt(LocalDateTime.now());

        return lineItemRepository.save(lineItem);
    }

    @Override
    public List<LineItem> getLineItemsByRequestId(Long requestId) {
        ProcurementRequest request = new ProcurementRequest();
        request.setId(requestId);
        return lineItemRepository.findByProcurementRequest(request);
    }

    @Override
    public LineItem getLineItemById(Long id) {
        return lineItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line item not found with id: " + id));
    }
}