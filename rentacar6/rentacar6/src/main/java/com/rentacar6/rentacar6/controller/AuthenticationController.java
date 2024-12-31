package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.model.Admin;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.AdminRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import com.rentacar6.rentacar6.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * POST /login endpoint for user and admin authentication
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            // Customer authentication
            Optional<Customer> customer = customerRepository.findByEmail(email);
            if (customer.isPresent() && passwordEncoder.matches(password, customer.get().getPassword())) {
                String token = jwtService.generateToken(email, "ROLE_USER");
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", "USER");
                response.put("email", email);
                return ResponseEntity.ok(response);
            }

            // Admin authentication
            Optional<Admin> admin = adminRepository.findByEmail(email);
            if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())) {
                String token = jwtService.generateToken(email, "ROLE_ADMIN");
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", "ADMIN");
                response.put("email", email);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Login failed for email {}: {}", email, e.getMessage());
        }

        // Invalid credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    /**
     * POST /register endpoint for user registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        try {
            // Check if email is already in use
            if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
            }

            // Assign default role
            if (customer.getRole() == null || customer.getRole().isEmpty()) {
                customer.setRole("ROLE_USER");
            }

            // Encode password and save
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            Customer savedCustomer = customerRepository.save(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * GET /me endpoint to get the authenticated user's details
     */
    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header is missing or invalid.");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String email = jwtService.extractUsername(token);

            // Find user by email
            Optional<Customer> customer = customerRepository.findByEmail(email);
            if (customer.isPresent()) {
                return ResponseEntity.ok(customer.get());
            }

            // If user is an admin
            Optional<Admin> admin = adminRepository.findByEmail(email);
            if (admin.isPresent()) {
                return ResponseEntity.ok(admin.get());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            logger.error("Error retrieving authenticated user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve authenticated user.");
        }
    }


        @GetMapping("/role")
        public ResponseEntity<Map<String, String>> getUserRole(Authentication authentication) {
            Map<String, String> response = new HashMap<>();
            if (authentication == null) {
                response.put("role", "guest");
            } else {
                response.put("role", authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ? "admin" : "user");
            }
            return ResponseEntity.ok(response);
        }


}
