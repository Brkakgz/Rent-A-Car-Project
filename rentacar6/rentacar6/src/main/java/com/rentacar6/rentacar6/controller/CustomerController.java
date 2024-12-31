package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.UpdateProfileRequest;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.service.CustomerService;
import com.rentacar6.rentacar6.service.CustomUserDetails;
import com.rentacar6.rentacar6.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }

   /*@GetMapping("/me")
    public ResponseEntity<Customer> getMyDetails(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // Kullanıcı kimlik doğrulaması yapılmış email bilgisi
        Customer customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(customer);
    }*/

    @GetMapping("/profile")
    public ResponseEntity<Customer> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String email = customUserDetails.getUsername(); // Oturum açmış kullanıcının email bilgisi
        Customer customer = customerService.getCustomerByEmail(email);
        customer.setPassword(null); // Hassas bilgiyi kaldır
        return ResponseEntity.ok(customer);
    }

    @Autowired
    private JwtService jwtService;

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest) {

        String email = customUserDetails.getUsername(); // Oturum açmış kullanıcının email bilgisi
        customerService.updateCustomerProfile(email, updateProfileRequest);

        // Yeni bir token oluştur ve döndür
        String newToken = jwtService.generateToken(updateProfileRequest.getEmail(), customUserDetails.getAuthorities().iterator().next().getAuthority());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully.");
        response.put("newToken", newToken);

        return ResponseEntity.ok(response);
    }
}

