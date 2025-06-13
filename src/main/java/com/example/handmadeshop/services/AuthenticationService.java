package com.example.handmadeshop.services;

import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.Utility.JwtUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@Stateless
public class AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private UserService userService;

    public String authenticate(String email, String password) {
        UserDTO userDTO = userService.findByEmail(email);

        if (userDTO == null) {
            return null; 
        }

        if (!userDTO.getPassword().equals(password)) {
            return null; 
        }

        String token = jwtUtil.generateToken(userDTO);
        return token;
    }
}