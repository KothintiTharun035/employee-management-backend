package com.ems;

import com.ems.model.User;
import com.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EmployeeManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {

            User user = userRepository.findByUsername("admin").orElse(null);

            if (user != null) {
                System.out.println("Username: " + user.getUsername());
                System.out.println("Password matches admin123: "
                        + passwordEncoder.matches("admin123", user.getPassword()));
            }
        };
    }
}