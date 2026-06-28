package com.ems.controller;

import com.ems.config.JwtUtil;
import com.ems.dto.AuthDTO.LoginRequest;
import com.ems.dto.AuthDTO.LoginResponse;
import com.ems.dto.AuthDTO.RegisterRequest;
import com.ems.model.User;
import com.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    System.out.println("===== LOGIN REQUEST =====");
    System.out.println("Username: " + request.getUsername());
    System.out.println("Password: " + request.getPassword());

    User user = userRepository.findByUsername(request.getUsername()).orElse(null);

    System.out.println("User found: " + (user != null));

    if (user != null) {
        System.out.println("DB Username: " + user.getUsername());
        System.out.println("Password matches: " +
                passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        System.out.println("Authentication SUCCESS");

    } catch (Exception e) {
        System.out.println("Authentication FAILED");
        e.printStackTrace();
        return ResponseEntity.status(401)
                .body(LoginResponse.builder().message("Invalid credentials").build());
    }

    UserDetails userDetails =
            userDetailsService.loadUserByUsername(request.getUsername());

    String token = jwtUtil.generateToken(userDetails);

    return ResponseEntity.ok(
            LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .message("Login successful")
                    .build()
    );
}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole() != null ? request.getRole() : "ROLE_USER");
        } catch (IllegalArgumentException e) {
            role = User.Role.ROLE_USER;
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
