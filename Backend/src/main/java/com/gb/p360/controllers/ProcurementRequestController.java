package com.gb.p360.controllers;

import com.gb.p360.data.ProcurementRequestDTO;
import com.gb.p360.data.RequestOwnerDTO;
import com.gb.p360.models.ProcurementRequest;
import com.gb.p360.service.interfaces.ProcurementRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Procurement Request API", description = "APIs for managing procurement requests")
public class ProcurementRequestController {

    private final ProcurementRequestService requestService;

    @Autowired
    public ProcurementRequestController(ProcurementRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @Operation(summary = "Create a new procurement request", description = "Creates a draft procurement request")
    public ResponseEntity<ProcurementRequest> createRequest(
            @RequestBody ProcurementRequestDTO requestDTO,
            Principal principal) {
        ProcurementRequest createdRequest = requestService.createRequest(requestDTO, principal.getName());
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing procurement request", description = "Updates a draft procurement request")
    public ResponseEntity<ProcurementRequest> updateRequest(
            @PathVariable Long id,
            @RequestBody ProcurementRequestDTO requestDTO,
            Principal principal) {
        ProcurementRequest updatedRequest = requestService.updateRequest(id, requestDTO, principal.getName());
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get requests by factory", description = "Returns procurement requests for a specific factory")
    public ResponseEntity<ProcurementRequest> getRequestById(@PathVariable Long id) {
        ProcurementRequest request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/factory/{factoryId}")
    @Operation(summary = "Get requests by factory", description = "Returns procurement requests for a specific factory")
    public ResponseEntity<List<ProcurementRequest>> getRequestsByFactory(@PathVariable Long factoryId) {
        List<ProcurementRequest> requests = requestService.getRequestsByFactory(factoryId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/factory/{factoryId}/status/{status}")
    @Operation(summary = "Get requests by factory and status", description = "Returns procurement requests for a specific factory filtered by status")
    public ResponseEntity<List<ProcurementRequest>> getRequestsByFactoryAndStatus(
            @PathVariable Long factoryId,
            @PathVariable String status) {
        List<ProcurementRequest> requests = requestService.getRequestsByFactoryAndStatus(factoryId, status);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/created-by-me")
    @Operation(summary = "Get requests created by the current user", description = "Returns procurement requests created by the current user")
    public ResponseEntity<List<ProcurementRequest>> getRequestsCreatedByMe(Principal principal) {
        List<ProcurementRequest> requests = requestService.getRequestsByCreator(principal.getName());
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/owned-by-me")
    @Operation(summary = "Get requests owned by the current user", description = "Returns procurement requests owned by the current user")
    public ResponseEntity<List<ProcurementRequest>> getRequestsOwnedByMe(Principal principal) {
        List<ProcurementRequest> requests = requestService.getRequestsByOwner(principal.getName());
        return ResponseEntity.ok(requests);
    }

    @GetMapping
    @Operation(summary = "Get requests for factories the current user has access to", description = "Returns paginated procurement requests for all factories the user has access to")
    public ResponseEntity<Page<ProcurementRequest>> getRequestsForUserFactories(
            @RequestParam(required = false) String status,
            Pageable pageable,
            Principal principal) {
        Page<ProcurementRequest> requests = requestService.getRequestsForUserFactories(
                principal.getName(), status, pageable);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit a procurement request", description = "Changes a draft request status to submitted")
    public ResponseEntity<ProcurementRequest> submitRequest(
            @PathVariable Long id,
            Principal principal) {
        ProcurementRequest submittedRequest = requestService.submitRequest(id, principal.getName());
        return ResponseEntity.ok(submittedRequest);
    }

    @PutMapping("/{id}/discard")
    @Operation(summary = "Discard a procurement request", description = "Changes a draft request status to discarded")
    public ResponseEntity<ProcurementRequest> discardRequest(
            @PathVariable Long id,
            Principal principal) {
        ProcurementRequest discardedRequest = requestService.discardRequest(id, principal.getName());
        return ResponseEntity.ok(discardedRequest);
    }

    @PutMapping("/{id}/owner")
    @Operation(summary = "Assign owner to a procurement request", description = "Assigns a purchase team member as the owner of a request")
    public ResponseEntity<ProcurementRequest> assignOwner(
            @PathVariable Long id,
            @RequestBody RequestOwnerDTO ownerDTO,
            Principal principal) {
        ProcurementRequest updatedRequest = requestService.assignOwner(id, ownerDTO.getOwnerId(), principal.getName());
        return ResponseEntity.ok(updatedRequest);
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone an existing procurement request", description = "Creates a new request by cloning an existing one")
    public ResponseEntity<ProcurementRequest> cloneRequest(
            @PathVariable Long id,
            Principal principal) {
        ProcurementRequest clonedRequest = requestService.cloneRequest(id, principal.getName());
        return new ResponseEntity<>(clonedRequest, HttpStatus.CREATED);
    }
}