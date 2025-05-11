package com.gb.p360.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.gb.p360.data.UserDTO;
import com.gb.p360.models.User;
import com.gb.p360.repository.UserRepository;
import com.gb.p360.security.jwt.JwtUtils;
import com.gb.p360.security.services.UserDetailsImpl;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gb.p360.payload.request.LoginRequest;
import com.gb.p360.payload.response.JwtResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "APIs for authenticating users")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "Login using credentials", description = "Login using credentials")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            log.info("User authenticated successfully: {}", userDetails.getUsername());

            // Fetch the full User entity with factories using the optimized query
            User user = userRepository.findByUsernameWithFactories(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
            
            log.info("Found user entity with ID: {}", user.getId());
            log.debug("User details - Factories: {}", user.getFactories().size());

            // Convert to DTO
            try {
                UserDTO userDTO = UserDTO.fromUser(user);
                log.debug("Successfully converted user to DTO");
                return ResponseEntity.ok(new JwtResponse(jwt, userDTO));
            } catch (Exception e) {
                log.error("Error converting user to DTO", e);
                throw e;
            }
        } catch (Exception e) {
            log.error("Error during login process", e);
            throw e;
        }
    }
}