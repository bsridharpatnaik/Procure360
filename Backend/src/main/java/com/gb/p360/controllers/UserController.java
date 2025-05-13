package com.gb.p360.controllers;

import com.gb.p360.config.UserMapper;
import com.gb.p360.data.UserDTO;
import com.gb.p360.models.User;
import com.gb.p360.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management API", description = "APIs for managing users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user's role and factories",
            description = "Updates role and factory assignments for a user")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "Returns a user based on the ID provided")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping
    @Operation(summary = "Get all users",
            description = "Returns a paginated list of users in the system")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(page = 0, size = 2, sort = "id") Pageable pageable) {
        Page<User> userPage = userService.getAllUsers(pageable);
        Page<UserDTO> userDTOPage = userMapper.toDTOPage(userPage);
        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role",
            description = "Returns users who have the specified role")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @GetMapping("/factory/{factoryId}")
    @Operation(summary = "Get users by factory",
            description = "Returns users who have access to the specified factory")
    public ResponseEntity<List<UserDTO>> getUsersByFactoryId(@PathVariable Long factoryId) {
        List<User> users = userService.getUsersByFactoryId(factoryId);
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    @PutMapping("/{userId}/factories")
    @Operation(summary = "Assign factories to a user",
            description = "Updates the factory assignments for a specific user")
    public ResponseEntity<UserDTO> assignFactoriesToUser(
            @PathVariable Long userId,
            @RequestBody Set<Long> factoryIds) {
        User updatedUser = userService.assignFactoriesToUser(userId, factoryIds);
        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }
}