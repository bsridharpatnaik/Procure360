package com.gb.p360.service;

import com.gb.p360.data.UserDTO;
import com.gb.p360.exception.ResourceNotFoundException;
import com.gb.p360.models.Factory;
import com.gb.p360.models.Role;
import com.gb.p360.models.User;
import com.gb.p360.repository.FactoryRepository;
import com.gb.p360.repository.UserRepository;
import com.gb.p360.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FactoryRepository factoryRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FactoryRepository factoryRepository) {
        this.userRepository = userRepository;
        this.factoryRepository = factoryRepository;
    }

    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Don't update username or password since those are handled by your security implementation
        user.setRole(Role.valueOf(userDTO.getRole()));

        if (userDTO.getFactoryIds() != null) {
            Set<Factory> factories = userDTO.getFactoryIds().stream()
                    .map(factoryId -> factoryRepository.findById(factoryId)
                            .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + factoryId)))
                    .collect(Collectors.toSet());
            user.setFactories(factories);
        }

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role));
    }

    @Override
    public List<User> getUsersByFactoryId(Long factoryId) {
        return userRepository.findByFactoriesId(factoryId);
    }

    @Override
    public User assignFactoriesToUser(Long userId, Set<Long> factoryIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Factory> factories = factoryIds.stream()
                .map(factoryId -> factoryRepository.findById(factoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Factory not found with id: " + factoryId)))
                .collect(Collectors.toSet());

        user.setFactories(factories);
        return userRepository.save(user);
    }
}