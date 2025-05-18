package com.gb.p360.config;

import com.gb.p360.data.VendorDTO;
import com.gb.p360.models.Vendor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VendorMapper {

    VendorDTO toDTO(Vendor vendor);

    List<VendorDTO> toDTOList(List<Vendor> vendors);

    // For paginated results
    default Page<VendorDTO> toDTOPage(Page<Vendor> vendorPage) {
        return vendorPage.map(this::toDTO);
    }
}
