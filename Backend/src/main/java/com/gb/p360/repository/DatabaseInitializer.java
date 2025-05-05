package com.gb.p360.repository;

import com.gb.p360.models.Role;
import com.gb.p360.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.Arrays;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {

        //return if non-local profile detected.
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch("local"::equalsIgnoreCase)) {
            System.out.println("Non-Local Profile found. Return without database initialization");
            return;
        }

        Optional<User> factoryOptional = userRepository.findByUsername("factory");
        if (!factoryOptional.isPresent()) {
            User raja = new User();
            raja.setUsername("factory");
            raja.setPassword("$2a$04$dzz91QUINWlllzzX7cK/TudKCZb5ZMlvCHdxEkx/nHUaX7d/dbFIa"); // Remember to hash this password in real use
            raja.setRole(Role.FACTORY_USER);
            userRepository.save(raja);
        }
        Optional<User> approverOptional = userRepository.findByUsername("approver");
        if (!approverOptional.isPresent()) {
            User raja = new User();
            raja.setUsername("approver");
            raja.setPassword("$2a$04$dzz91QUINWlllzzX7cK/TudKCZb5ZMlvCHdxEkx/nHUaX7d/dbFIa"); // Remember to hash this password in real use
            raja.setRole(Role.APPROVAL_TEAM);
            userRepository.save(raja);
        }

        Optional<User> purchaseOptional = userRepository.findByUsername("purchase");
        if (!purchaseOptional.isPresent()) {
            User raja = new User();
            raja.setUsername("raja");
            raja.setPassword("$2a$04$dzz91QUINWlllzzX7cK/TudKCZb5ZMlvCHdxEkx/nHUaX7d/dbFIa"); // Remember to hash this password in real use
            raja.setRole(Role.PURCHASE_TEAM);
            userRepository.save(raja);
        }
    }
}