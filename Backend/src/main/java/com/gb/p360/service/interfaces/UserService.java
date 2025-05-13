package com.gb.p360.service.interfaces;

import com.gb.p360.data.UserDTO;
import com.gb.p360.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Set;

public interface UserService {
    User updateUser(Long id, UserDTO userDTO);
    User getUserById(Long id);
    User getUserByUsername(String username);
    Page<User> getAllUsers(Pageable pageable);
    List<User> getUsersByRole(String role);
    List<User> getUsersByFactoryId(Long factoryId);
    User assignFactoriesToUser(Long userId, Set<Long> factoryIds);
}