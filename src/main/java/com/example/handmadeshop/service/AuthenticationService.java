package com.example.handmadeshop.service;

import com.example.handmadeshop.EJB.model.Role;
import com.example.handmadeshop.EJB.model.User;
import com.example.handmadeshop.EJB.model.UserRole;
import com.example.handmadeshop.DTO.AuthResponseDTO;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.Utility.JwtUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private UserService userService;

    public AuthResponseDTO authenticate(String email, String password) {
        User user = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            List<String> roles = user.getUserRoles().stream()
                    .map(UserRole::getRoleid)
                    .map(Role::getName)
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roles);

            return new AuthResponseDTO(
                    token,
                    userService.toDTO(user)
            );
        }

        return null;
    }

    public AuthResponseDTO authenticateGoogle(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = entityManager.find(User.class, userDTO.getId());
        if (user == null) return null;

        List<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRoleid)
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roles);

        return new AuthResponseDTO(token, userDTO);
    }
}