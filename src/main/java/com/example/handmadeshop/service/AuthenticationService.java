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
    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private UserService userService;

    public String authenticate(String email, String password) {
        UserDTO userDTO = userService.findByEmail(email);
        if(userDTO == null) return null;
        String token = jwtUtil.generateToken(userDTO);
        return token;

        }


}