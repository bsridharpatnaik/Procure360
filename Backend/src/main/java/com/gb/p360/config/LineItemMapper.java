package com.gb.p360.config;

import com.gb.p360.data.LineItemDTO;
import com.gb.p360.models.LineItem;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LineItemMapper {

    LineItemDTO toDTO(LineItem lineItem);

    List<LineItemDTO> toDTOList(List<LineItem> lineItems);

    // For paginated results
    default Page<LineItemDTO> toDTOPage(Page<LineItem> lineItemPage) {
        return lineItemPage.map(this::toDTO);
    }
}
