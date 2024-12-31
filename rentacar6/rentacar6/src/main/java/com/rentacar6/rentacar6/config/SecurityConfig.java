package com.rentacar6.rentacar6.config;

import com.rentacar6.rentacar6.filter.JwtAuthenticationFilter;
import com.rentacar6.rentacar6.model.Admin;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.AdminRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Rol bazlı kontrol için
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(email -> {
            // Kullanıcı veya Admin doğrulaması
            Optional<Customer> customer = customerRepository.findByEmail(email);
            if (customer.isPresent()) {
                return new org.springframework.security.core.userdetails.User(
                        customer.get().getEmail(),
                        customer.get().getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }

            Optional<Admin> admin = adminRepository.findByEmail(email);
            if (admin.isPresent()) {
                return new org.springframework.security.core.userdetails.User(
                        admin.get().getEmail(),
                        admin.get().getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
            }

            throw new UsernameNotFoundException("User not found with email: " + email);
        });
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Misafir kullanıcılar için izin verilen uç noktalar
                        .requestMatchers("/api/cars/allCars", "/api/cars/locations", "/api/cars/gear-types", "/api/cars/fuel-types", "/api/cars/filteredCars")
                        .permitAll()
                        // Statik dosya yollarını herkese açık yap
                        .requestMatchers("/**", "/static/**", "/templates/**", "/index.html", "/js/**", "/css/**", "/images/**").permitAll()
                        // Swagger ve Auth endpointlerini açık tut
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**").permitAll()
                        // Araç GET işlemleri herkese açık
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()
                        // Sipariş işlemleri sadece USER erişimine açık
                        .requestMatchers("/api/orders/**").hasRole("USER")
                        // Admin işlemleri sadece ADMIN erişimine açık
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/cars/**").hasRole("ADMIN")
                        // Diğer istekler için kimlik doğrulaması gerekli
                        .anyRequest().authenticated()
                )
                // Stateless session yapılandırması
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT filtrelerini kullan
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
        }

}
