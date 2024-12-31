package com.rentacar6.rentacar6.filter;

import com.rentacar6.rentacar6.service.JwtService;
import com.rentacar6.rentacar6.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/register")) {
            chain.doFilter(request, response);
            return;
        }

        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                logger.error("JWT Token parsing failed: " + e.getMessage());
                // Token hatalı ise misafir kullanıcı olarak devam et
                chain.doFilter(request, response);
                return;
            }
        }

        if (Objects.nonNull(email) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            try {
                // Kullanıcının detaylarını yükle
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Rolü olduğu gibi kullan
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .orElse("");

                // JWT doğrulama
                if (jwtService.validateToken(token, email, role)) {
                    // Kullanıcıyı doğrula ve güvenlik bağlamına ekle
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Hatalı token veya rol uyuşmazlığı
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token or role mismatch");
                    return;
                }
            } catch (Exception e) {
                // JWT doğrulama hatası
                logger.error("JWT validation failed: " + e.getMessage());
                // Doğrulama hatasında misafir kullanıcı olarak devam et
                chain.doFilter(request, response);
                return;
            }
        }

        // İstek zincirini devam ettir
        chain.doFilter(request, response);
    }
}
