package com.rentacar6.rentacar6.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    /**
     * Extract email (subject) from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract username (alias for extractEmail)
     */
    public String extractUsername(String token) {
        return extractEmail(token); // Same functionality
    }

    /**
     * Extract a specific claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validate token with email and role
     */
    public boolean validateToken(String token, String email, String role) {
        try {
            if (isTokenExpired(token)) {
                System.err.println("Token has expired.");
                return false;
            }
            final String extractedEmail = extractEmail(token);
            final String extractedRole = extractClaim(token, claims -> claims.get("role", String.class));
            return extractedEmail.equals(email) && extractedRole.equals(role);
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate token with email and role
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        Date expirationDate = new Date(currentTimeMillis + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (IllegalArgumentException e) {
            System.err.println("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }

    public Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new IllegalArgumentException("Token expiration could not be extracted: " + e.getMessage());
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(30) // Allow 30 seconds clock skew
                .build();

        try {
            return parser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            System.err.println("Error extracting claims from token: " + e.getMessage());
            throw e;
        }
    }
}
