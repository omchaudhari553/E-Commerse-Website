package com.springboot.auth.service;

import com.springboot.auth.dto.AuthResponse;
import com.springboot.auth.dto.LoginRequest;
import com.springboot.auth.dto.PasswordResetRequest;
import com.springboot.auth.entity.User;
import com.springboot.auth.repository.UserRepository;
import com.springboot.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        user.setEmail(user.getEmail().trim().toLowerCase());
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());
        return new AuthResponse(savedUser, token, "Registration successful");
    }
    
    public AuthResponse login(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        final String email = loginRequest.getEmail().trim().toLowerCase();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(user, token, "Login successful");
    }
    
    public AuthResponse adminLogin(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        final String email = loginRequest.getEmail().trim().toLowerCase();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        if (!"ADMIN".equals(user.getRole().toUpperCase())) {
            throw new IllegalArgumentException("Unauthorized access. Admin privileges required.");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(user, token, "Login successful");
    }
    
    @Transactional
    public void resetPassword(PasswordResetRequest resetRequest) {
        if (resetRequest.getEmail() == null || resetRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        final String email = resetRequest.getEmail().trim().toLowerCase();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        
        if (resetRequest.getNewPassword() == null || resetRequest.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }
        
        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        userRepository.save(user);
    }
    
    public String generatePasswordResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        final String trimmedEmail = email.trim().toLowerCase();
        
        User user = userRepository.findByEmail(trimmedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + trimmedEmail));
        
        String resetToken = UUID.randomUUID().toString();
        
        return resetToken;
    }
}
