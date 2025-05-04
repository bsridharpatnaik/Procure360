package com.gb.p360.controllers;

import com.gb.p360.data.UserDTO;
import com.gb.p360.models.User;
import com.gb.p360.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management API", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user's role and factories",
            description = "Updates role and factory assignments for a user")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "Returns a user based on the ID provided")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get all users",
            description = "Returns a list of all users in the system")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role",
            description = "Returns users who have the specified role")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/factory/{factoryId}")
    @Operation(summary = "Get users by factory",
            description = "Returns users who have access to the specified factory")
    public ResponseEntity<List<User>> getUsersByFactoryId(@PathVariable Long factoryId) {
        List<User> users = userService.getUsersByFactoryId(factoryId);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/factories")
    @Operation(summary = "Assign factories to a user",
            description = "Updates the factory assignments for a specific user")
    public ResponseEntity<User> assignFactoriesToUser(
            @PathVariable Long userId,
            @RequestBody Set<Long> factoryIds) {
        User updatedUser = userService.assignFactoriesToUser(userId, factoryIds);
        return ResponseEntity.ok(updatedUser);
    }
}