package com.gb.p360.repository;

import com.gb.p360.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Ensure 'raja' user exists with 'egcity' organization
        Optional<User> rajaOptional = userRepository.findByUsername("raja");
        if (!rajaOptional.isPresent()) {
            User raja = new User();
            raja.setUsername("raja");
            raja.setPassword("$2a$04$dzz91QUINWlllzzX7cK/TudKCZb5ZMlvCHdxEkx/nHUaX7d/dbFIa"); // Remember to hash this password in real use
            userRepository.save(raja);
        }
    }
}