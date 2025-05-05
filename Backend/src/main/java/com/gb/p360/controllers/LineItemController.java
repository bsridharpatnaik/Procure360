package com.gb.p360.controllers;

import com.gb.p360.data.LineItemDTO;
import com.gb.p360.data.LineItemStatusUpdateDTO;
import com.gb.p360.models.LineItem;
import com.gb.p360.service.interfaces.LineItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/lineitems")
@Tag(name = "Line Item API", description = "APIs for managing line items in procurement requests")
public class LineItemController {

    private final LineItemService lineItemService;

    @Autowired
    public LineItemController(LineItemService lineItemService) {
        this.lineItemService = lineItemService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get line item by ID", description = "Returns a line item based on the ID provided")
    public ResponseEntity<LineItem> getLineItemById(@PathVariable Long id) {
        LineItem lineItem = lineItemService.getLineItemById(id);
        return ResponseEntity.ok(lineItem);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "Get line items by request ID", description = "Returns all line items for a specific procurement request")
    public ResponseEntity<List<LineItem>> getLineItemsByRequestId(@PathVariable Long requestId) {
        List<LineItem> lineItems = lineItemService.getLineItemsByRequestId(requestId);
        return ResponseEntity.ok(lineItems);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update line item status", description = "Updates the status of a line item")
    public ResponseEntity<LineItem> updateLineItemStatus(
            @PathVariable Long id,
            @RequestBody LineItemStatusUpdateDTO updateDTO,
            Principal principal) {
        LineItem updatedLineItem = lineItemService.updateLineItemStatus(id, updateDTO, principal.getName());
        return ResponseEntity.ok(updatedLineItem);
    }

    @PutMapping("/{id}/order-details")
    @Operation(summary = "Update line item order details", description = "Updates vendor, price, and quantity details for a line item")
    public ResponseEntity<LineItem> updateOrderDetails(
            @PathVariable Long id,
            @RequestBody LineItemDTO lineItemDTO,
            Principal principal) {
        LineItem updatedLineItem = lineItemService.updateOrderDetails(id, lineItemDTO, principal.getName());
        return ResponseEntity.ok(updatedLineItem);
    }

    @PutMapping("/{id}/receive")
    @Operation(summary = "Mark line item as received", description = "Changes a line item status to received")
    public ResponseEntity<LineItem> markAsReceived(
            @PathVariable Long id,
            @RequestParam String remarks,
            Principal principal) {
        LineItem receivedLineItem = lineItemService.markAsReceived(id, remarks, principal.getName());
        return ResponseEntity.ok(receivedLineItem);
    }
}