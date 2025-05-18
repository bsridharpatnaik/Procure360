package com.gb.p360.config;

import com.gb.p360.data.PriceHistoryDTO;
import com.gb.p360.models.PriceHistory;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceHistoryMapper {

    PriceHistoryDTO toDTO(PriceHistory priceHistory);

    List<PriceHistoryDTO> toDTOList(List<PriceHistory> priceHistories);

    // For paginated results
    default Page<PriceHistoryDTO> toDTOPage(Page<PriceHistory> priceHistoryPage) {
        return priceHistoryPage.map(this::toDTO);
    }
}
