package com.gb.p360.config;

import com.gb.p360.data.ProcurementRequestDTO;
import com.gb.p360.models.ProcurementRequest;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProcurementRequestMapper {

    ProcurementRequestDTO toDTO(ProcurementRequest procurementRequest);

    List<ProcurementRequestDTO> toDTOList(List<ProcurementRequest> procurementRequests);

    // For paginated results
    default Page<ProcurementRequestDTO> toDTOPage(Page<ProcurementRequest> procurementRequestPage) {
        return procurementRequestPage.map(this::toDTO);
    }
}
