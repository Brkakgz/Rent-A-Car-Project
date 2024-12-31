package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.model.Admin;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.AdminRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Admin kontrolü
        Optional<Admin> admin = adminRepository.findByEmail(username);
        if (admin.isPresent()) {
            Admin adminEntity = admin.get();
            return new CustomUserDetails(
                    adminEntity.getFirstName(),
                    adminEntity.getLastName(),
                    null, // Admin için TC bilgisi yoksa
                    adminEntity.getPhone(),
                    adminEntity.getAddress(),
                    adminEntity.getEmail(),
                    adminEntity.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(adminEntity.getRole()))
            );
        }

        // Customer kontrolü
        Optional<Customer> customer = customerRepository.findByEmail(username);
        if (customer.isPresent()) {
            Customer customerEntity = customer.get();
            return new CustomUserDetails(
                    customerEntity.getFirstName(),
                    customerEntity.getLastName(),
                    customerEntity.getTcNo(),
                    customerEntity.getPhone(),
                    customerEntity.getAddress(),
                    customerEntity.getEmail(),
                    customerEntity.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(customerEntity.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}

