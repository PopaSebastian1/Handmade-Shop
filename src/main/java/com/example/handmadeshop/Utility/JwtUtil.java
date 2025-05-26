package com.example.handmadeshop.Utility;

import com.example.handmadeshop.DTO.UserDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class JwtUtil {
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public String generateToken(UserDTO user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles())
                .claim("surname", user.getSurname())
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.log(Level.WARNING, "Invalid JWT token: {0}", e.getMessage());
            return null;
        }
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("roles", List.class);
        }
        return List.of();
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("userId", Integer.class);
        }
        return null;
    }
    public Claims getClaims(String token) {
        try {
            return validateToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    public boolean hasRole(String token, String requiredRole) {
        try {
            Claims claims = validateToken(token);
            if (claims == null) return false;

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            // Debug logging
            System.out.println("Required role: " + requiredRole);
            System.out.println("User roles: " + roles);

            return roles != null && roles.contains(requiredRole);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}