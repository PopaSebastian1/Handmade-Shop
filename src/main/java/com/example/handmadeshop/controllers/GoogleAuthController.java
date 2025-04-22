package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.AuthResponseDTO;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.service.AuthenticationService;
import com.example.handmadeshop.service.GoogleAuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class GoogleAuthController {

    @Inject
    private GoogleAuthService googleAuthService;

    @Inject
    private AuthenticationService authenticationService;

    @POST
    @Path("/google/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateWithGoogle(GoogleTokenRequest tokenRequest) {
        try {
            if (tokenRequest == null || tokenRequest.getIdToken() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing ID token")
                        .build();
            }

            String token = tokenRequest.getIdToken();
            System.out.println("Google Auth: Received token (first 20 chars): " + token.substring(0, 20) + "...");

            // Check if user exists
            UserDTO existingUser = googleAuthService.findGoogleUser(token);

            // User exists - proceed with login
            if (existingUser != null) {
                AuthResponseDTO authResponse = authenticationService.authenticateGoogle(existingUser);
                return Response.ok(authResponse).build();
            }

            // User doesn't exist - register new user
            UserDTO newUser = googleAuthService.registerGoogleUser(token);
            AuthResponseDTO authResponse = authenticationService.authenticateGoogle(newUser);
            return Response.ok(authResponse).build();
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Google authentication failed: " + e.getMessage())
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