package com.gb.p360.config;

import com.gb.p360.data.FactoryDTO;
import com.gb.p360.data.UserDTO;
import com.gb.p360.models.Factory;
import com.gb.p360.models.User;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FactoryMapper {

    FactoryDTO toDTO(Factory factory);

    List<FactoryDTO> toDTOList(List<Factory> factories);

    // For paginated results
    default Page<FactoryDTO> toDTOPage(Page<Factory> factoryPage) {
        return factoryPage.map(this::toDTO);
    }
}