package com.gb.p360.data;

import com.gb.p360.models.Factory;
import com.gb.p360.models.Role;
import com.gb.p360.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
    private Set<FactoryDTO> factories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactoryDTO {
        private Long id;
        private String name;
        private String code;
    }

    public static UserDTO fromUser(User user) {
        try {
            log.debug("Starting conversion of User to UserDTO. User ID: {}", user.getId());
            UserDTO dto = new UserDTO();
            
            log.debug("Setting basic fields");
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setUpdatedAt(user.getUpdatedAt());

            // Convert factories to simplified DTOs to avoid circular references
            if (user.getFactories() != null) {
                log.debug("Converting factories. Number of factories: {}", user.getFactories().size());
                try {
                    Set<FactoryDTO> factoryDTOs = user.getFactories().stream()
                            .map(factory -> {
                                log.trace("Processing factory: {}", factory.getId());
                                return new FactoryDTO(
                                    factory.getId(),
                                    factory.getName(),
                                    factory.getCode()
                                );
                            })
                            .collect(Collectors.toSet());
                    dto.setFactories(factoryDTOs);
                    log.debug("Successfully converted factories to DTOs");
                } catch (Exception e) {
                    log.error("Error while converting factories to DTOs", e);
                    throw e;
                }
            } else {
                log.debug("No factories to convert");
                dto.setFactories(new HashSet<>());
            }
            
            log.debug("Successfully completed UserDTO conversion");
            return dto;
        } catch (Exception e) {
            log.error("Error during User to UserDTO conversion", e);
            throw e;
        }
    }
}