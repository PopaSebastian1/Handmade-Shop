package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.service.GoogleAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class GoogleAuthController {

    @Inject
    private GoogleAuthService googleAuthService;

    @POST
    @Path("/google/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginWithGoogle(GoogleTokenRequest tokenRequest) {
        try {
            if (tokenRequest == null || tokenRequest.getIdToken() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing ID token")
                        .build();
            }

            String token = tokenRequest.getIdToken();
            System.out.println("Login: Received token (first 20 chars): " + token.substring(0, 20) + "...");

            UserDTO user = googleAuthService.loginGoogleUser(token);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User not found. Please register first.")
                        .build();
            }
            return Response.ok(user).build();
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Google login failed: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/google/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerWithGoogle(GoogleTokenRequest tokenRequest) {
        try {
            if (tokenRequest == null || tokenRequest.getIdToken() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing ID token")
                        .build();
            }

            String token = tokenRequest.getIdToken();
            System.out.println("Register: Received token (first 20 chars): " + token.substring(0, 20) + "...");

            // Check if user already exists
            UserDTO existingUser = googleAuthService.findGoogleUser(token);
            if (existingUser != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("User already exists. Please login instead.")
                        .build();
            }

            UserDTO newUser = googleAuthService.registerGoogleUser(token);
            return Response.ok(newUser).build();
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Google registration failed: " + e.getMessage())
                    .build();
        }
    }

    public static class GoogleTokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}