package com.gb.p360.service.interfaces;

import com.gb.p360.data.ProcurementRequestDTO;
import com.gb.p360.models.ProcurementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProcurementRequestService {
    ProcurementRequest createRequest(ProcurementRequestDTO requestDTO, String username);

    ProcurementRequest updateRequest(Long id, ProcurementRequestDTO requestDTO, String username);

    ProcurementRequest getRequestById(Long id);

    List<ProcurementRequest> getRequestsByFactory(Long factoryId);

    List<ProcurementRequest> getRequestsByFactoryAndStatus(Long factoryId, String status);

    List<ProcurementRequest> getRequestsByCreator(String username);

    List<ProcurementRequest> getRequestsByOwner(String username);

    Page<ProcurementRequest> getRequestsForUserFactories(String username, String status, Pageable pageable);

    ProcurementRequest submitRequest(Long id, String username);

    ProcurementRequest discardRequest(Long id, String username);

    ProcurementRequest assignOwner(Long requestId, Long ownerId, String username);

    ProcurementRequest cloneRequest(Long id, String username);
}