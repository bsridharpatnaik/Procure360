package com.gb.p360.config;

import com.gb.p360.data.MaterialDTO;
import com.gb.p360.models.Material;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    MaterialDTO toDTO(Material material);

    List<MaterialDTO> toDTOList(List<Material> materials);

    // For paginated results
    default Page<MaterialDTO> toDTOPage(Page<Material> materialPage) {
        return materialPage.map(this::toDTO);
    }
}
