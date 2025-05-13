package com.gb.p360.config;

import com.gb.p360.data.UserDTO;
import com.gb.p360.models.User;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    // For paginated results
    default Page<UserDTO> toDTOPage(Page<User> userPage) {
        return userPage.map(this::toDTO);
    }
}