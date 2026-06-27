package com.ems.config;

import com.ems.model.User;
import com.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        User admin = userRepository.findByUsername("admin")
                .orElse(User.builder()
                        .username("admin")
                        .role(User.Role.ROLE_ADMIN)
                        .build());

        admin.setPassword(passwordEncoder.encode("admin123"));

        userRepository.save(admin);

        System.out.println("Admin account initialized.");
    }
}