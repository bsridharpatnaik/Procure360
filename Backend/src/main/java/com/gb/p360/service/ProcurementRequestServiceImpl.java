package com.gb.p360.service;

import com.gb.p360.data.LineItemDTO;
import com.gb.p360.data.ProcurementRequestDTO;
import com.gb.p360.exception.AccessDeniedException;
import com.gb.p360.exception.DuplicateRequestException;
import com.gb.p360.exception.InvalidOperationException;
import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.*;
import com.gb.p360.repository.FactoryRepository;
import com.gb.p360.repository.LineItemRepository;
import com.gb.p360.repository.ProcurementRequestRepository;
import com.gb.p360.repository.UserRepository;
import com.gb.p360.service.interfaces.MaterialService;
import com.gb.p360.service.interfaces.ProcurementRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcurementRequestServiceImpl implements ProcurementRequestService {

    private final ProcurementRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final FactoryRepository factoryRepository;
    private final MaterialService materialService;
    private final LineItemRepository lineItemRepository;

    @Autowired
    public ProcurementRequestServiceImpl(
            ProcurementRequestRepository requestRepository,
            UserRepository userRepository,
            FactoryRepository factoryRepository,
            MaterialService materialService,
            LineItemRepository lineItemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.factoryRepository = factoryRepository;
        this.materialService = materialService;
        this.lineItemRepository = lineItemRepository;
    }

    @Override
    public ProcurementRequest createRequest(ProcurementRequestDTO requestDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (user.getRole() != Role.FACTORY_USER) {
            throw new AccessDeniedException("Only factory users can create procurement requests");
        }

        Factory factory = factoryRepository.findById(requestDTO.getFactoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + requestDTO.getFactoryId()));

        // Check if user has access to this factory
        if (user.getFactories().stream().noneMatch(f -> f.getId().equals(factory.getId()))) {
            throw new AccessDeniedException("User does not have access to this factory");
        }

        // Generate unique identifier
        String uniqueIdentifier = generateUniqueIdentifier(factory.getCode());

        ProcurementRequest request = new ProcurementRequest();
        request.setUniqueIdentifier(uniqueIdentifier);
        request.setFactory(factory);
        request.setPriority(Priority.valueOf(requestDTO.getPriority()));
        request.setRequestDate(LocalDateTime.now());
        request.setRemarks(requestDTO.getRemarks());
        request.setStatus(RequestStatus.DRAFTED);
        request.setCreatedBy(user);

        // Save the request first to get ID
        ProcurementRequest savedRequest = requestRepository.save(request);

        // Add line items
        if (requestDTO.getLineItems() != null && !requestDTO.getLineItems().isEmpty()) {
            List<LineItem> lineItems = createLineItems(requestDTO.getLineItems(), savedRequest);
            savedRequest.setLineItems(lineItems);
        }

        // Check for duplicate requests in last 10 seconds
        checkDuplicateRequest(factory, savedRequest);

        return requestRepository.save(savedRequest);
    }

    private String generateUniqueIdentifier(String factoryCode) {
        Optional<Integer> maxNumber = requestRepository.findMaxRequestNumberByFactoryCode(factoryCode);
        int nextNumber = maxNumber.orElse(0) + 1;
        return String.format("%s-%06d", factoryCode, nextNumber);
    }

    private List<LineItem> createLineItems(List<LineItemDTO> lineItemDTOs, ProcurementRequest request) {
        List<LineItem> lineItems = new ArrayList<>();

        for (LineItemDTO dto : lineItemDTOs) {
            Material material = materialService.findOrCreateMaterial(dto.getMaterialName(), dto.getUnit());

            LineItem lineItem = new LineItem();
            lineItem.setProcurementRequest(request);
            lineItem.setMaterial(material);
            lineItem.setUnit(dto.getUnit());
            lineItem.setRequestedQuantity(dto.getRequestedQuantity());

            lineItems.add(lineItem);
        }

        return lineItemRepository.saveAll(lineItems);
    }

    private void checkDuplicateRequest(Factory factory, ProcurementRequest newRequest) {
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(10);
        List<ProcurementRequest> recentRequests = requestRepository.findByFactoryAndCreatedAtAfter(factory, tenSecondsAgo);

        if (!recentRequests.isEmpty()) {
            throw new DuplicateRequestException("A similar request was submitted within the last 10 seconds. Please wait before submitting again.");
        }
    }

    @Override
    public ProcurementRequest updateRequest(Long id, ProcurementRequestDTO requestDTO, String username) {
        ProcurementRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only the creator can update the request and only if it's in DRAFTED status
        if (!request.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the creator can update the request");
        }

        if (request.getStatus() != RequestStatus.DRAFTED) {
            throw new InvalidOperationException("Only draft requests can be updated");
        }

        // Update request fields
        request.setPriority(Priority.valueOf(requestDTO.getPriority()));
        request.setRemarks(requestDTO.getRemarks());

        // Handle line items
        if (requestDTO.getLineItems() != null) {
            // Clear existing line items
            lineItemRepository.deleteAll(request.getLineItems());
            request.getLineItems().clear();

            // Create new line items
            List<LineItem> lineItems = createLineItems(requestDTO.getLineItems(), request);
            request.setLineItems(lineItems);
        }

        return requestRepository.save(request);
    }

    @Override
    public ProcurementRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));
    }

    @Override
    public List<ProcurementRequest> getRequestsByFactory(Long factoryId) {
        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + factoryId));
        return requestRepository.findByFactory(factory);
    }

    @Override
    public List<ProcurementRequest> getRequestsByFactoryAndStatus(Long factoryId, String status) {
        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + factoryId));
        return requestRepository.findByFactoryAndStatus(factory, RequestStatus.valueOf(status));
    }

    @Override
    public List<ProcurementRequest> getRequestsByCreator(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return requestRepository.findByCreatedBy(user);
    }

    @Override
    public List<ProcurementRequest> getRequestsByOwner(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return requestRepository.findByOwner(user);
    }

    @Override
    public Page<ProcurementRequest> getRequestsForUserFactories(String username, String status, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Factory> userFactories = new ArrayList<>(user.getFactories());
        RequestStatus requestStatus = status != null ? RequestStatus.valueOf(status) : null;

        if (requestStatus != null) {
            return requestRepository.findByFactoryInAndStatus(userFactories, requestStatus, pageable);
        } else {
            return requestRepository.findAll(pageable);
        }
    }

    @Override
    public ProcurementRequest submitRequest(Long id, String username) {
        ProcurementRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only the creator can submit the request
        if (!request.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the creator can submit the request");
        }

        // Only draft requests can be submitted
        if (request.getStatus() != RequestStatus.DRAFTED) {
            throw new InvalidOperationException("Only draft requests can be submitted");
        }

        // Ensure request has line items
        if (request.getLineItems() == null || request.getLineItems().isEmpty()) {
            throw new InvalidOperationException("Cannot submit a request without line items");
        }

        // Update status and submission timestamp
        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public ProcurementRequest discardRequest(Long id, String username) {
        ProcurementRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only the creator can discard the request
        if (!request.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the creator can discard the request");
        }

        // Only draft requests can be discarded
        if (request.getStatus() != RequestStatus.DRAFTED) {
            throw new InvalidOperationException("Only draft requests can be discarded");
        }

        request.setStatus(RequestStatus.DISCARDED);
        return requestRepository.save(request);
    }

    @Override
    public ProcurementRequest assignOwner(Long requestId, Long ownerId, String username) {
        ProcurementRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + requestId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Only purchase team members can assign owners
        if (currentUser.getRole() != Role.PURCHASE_TEAM) {
            throw new AccessDeniedException("Only purchase team members can assign owners");
        }

        // Only submitted requests can have owners assigned
        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new InvalidOperationException("Only submitted requests can have owners assigned");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        // Owner must be a purchase team member
        if (owner.getRole() != Role.PURCHASE_TEAM) {
            throw new InvalidOperationException("Owner must be a purchase team member");
        }

        // Owner must have access to the factory
        if (owner.getFactories().stream().noneMatch(f -> f.getId().equals(request.getFactory().getId()))) {
            throw new InvalidOperationException("Owner does not have access to this factory");
        }

        request.setOwner(owner);
        return requestRepository.save(request);
    }

    @Override
    public ProcurementRequest cloneRequest(Long id, String username) {
        ProcurementRequest originalRequest = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement request not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // User must have access to the factory
        if (user.getFactories().stream().noneMatch(f -> f.getId().equals(originalRequest.getFactory().getId()))) {
            throw new AccessDeniedException("User does not have access to this factory");
        }

        // Generate a new unique identifier
        String uniqueIdentifier = generateUniqueIdentifier(originalRequest.getFactory().getCode());

        // Create new request with copied data
        ProcurementRequest newRequest = new ProcurementRequest();
        newRequest.setUniqueIdentifier(uniqueIdentifier);
        newRequest.setFactory(originalRequest.getFactory());
        newRequest.setPriority(originalRequest.getPriority());
        newRequest.setRequestDate(LocalDateTime.now());
        newRequest.setRemarks(originalRequest.getRemarks());
        newRequest.setStatus(RequestStatus.DRAFTED);
        newRequest.setCreatedBy(user);

        // Save the request first to get ID
        ProcurementRequest savedRequest = requestRepository.save(newRequest);

        // Clone line items
        List<LineItem> newLineItems = new ArrayList<>();
        for (LineItem originalItem : originalRequest.getLineItems()) {
            LineItem newItem = new LineItem();
            newItem.setProcurementRequest(savedRequest);
            newItem.setMaterial(originalItem.getMaterial());
            newItem.setUnit(originalItem.getUnit());
            newItem.setRequestedQuantity(originalItem.getRequestedQuantity());
            newLineItems.add(newItem);
        }

        lineItemRepository.saveAll(newLineItems);
        savedRequest.setLineItems(newLineItems);

        return savedRequest;
    }
}