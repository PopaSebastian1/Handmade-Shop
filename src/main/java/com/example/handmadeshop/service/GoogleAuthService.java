package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.UserDTO;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
public class GoogleAuthService {
    private static final Logger logger = Logger.getLogger(GoogleAuthService.class.getName());

    @Inject
    private UserService userService;

    private static final String CLIENT_ID = "251639296822-2rts40g7g70i0lsfv3d4uriic2597bbi.apps.googleusercontent.com";

    private UserDTO verifyGoogleToken(String idTokenString) throws Exception {
        try {
            logger.info("Attempting to verify Google token: " + idTokenString.substring(0, 20) + "...");

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                logger.warning("Google ID token verification failed");
                throw new Exception("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String subject = payload.getSubject(); // This is the Google user ID

            logger.info("Google user ID (subject): " + subject);

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(payload.getEmail());
            userDTO.setName((String) payload.get("given_name"));
            userDTO.setSurname((String) payload.get("family_name"));
            userDTO.setClientId(subject); // Set clientId to the subject value

            if (userDTO.getName() == null) userDTO.setName("Google");
            if (userDTO.getSurname() == null) userDTO.setSurname("User");

            logger.info("Google token verification successful for email: " + userDTO.getEmail() + " with clientId: " + userDTO.getClientId());

            return userDTO;
        } catch (GeneralSecurityException | IOException e) {
            logger.log(Level.SEVERE, "Error verifying Google token", e);
            throw new Exception("Error verifying Google token: " + e.getMessage(), e);
        }
    }

    public UserDTO findGoogleUser(String idTokenString) throws Exception {
        UserDTO googleUserInfo = verifyGoogleToken(idTokenString);
        logger.info("Looking for user with email: " + googleUserInfo.getEmail());

        return userService.findByEmail(googleUserInfo.getEmail());
    }

    public UserDTO loginGoogleUser(String idTokenString) throws Exception {
        UserDTO existingUser = findGoogleUser(idTokenString);

        if (existingUser == null) {
            logger.warning("Login failed - user not found");
            return null;
        }

        logger.info("Google login successful for existing user");
        return existingUser;
    }
    /**
     * Register a new Google user
     */
    public UserDTO registerGoogleUser(String idTokenString) throws Exception {
        try {
            UserDTO googleUserInfo = verifyGoogleToken(idTokenString);
            logger.info("Creating new user from Google info for email: " + googleUserInfo.getEmail());

            // First check if user already exists
            UserDTO existingUser = userService.findByEmail(googleUserInfo.getEmail());
            if (existingUser != null) {
                logger.warning("Registration failed - user already exists with email: " + googleUserInfo.getEmail());
                return existingUser; // Return existing user instead of null
            }

            logger.info("Creating new user with clientId: " + googleUserInfo.getClientId());

            UserDTO newUser = new UserDTO();
            newUser.setEmail(googleUserInfo.getEmail());
            newUser.setName(googleUserInfo.getName());
            newUser.setSurname(googleUserInfo.getSurname());
            newUser.setClientId(googleUserInfo.getClientId()); // Ensure this is set correctly
            newUser.setPassword(java.util.UUID.randomUUID().toString());
           // newUser.setRoles(Arrays.asList("viewer"));

            // Debug log to check if clientId is being set properly
            logger.info("About to create user with clientId: " + newUser.getClientId());

            UserDTO createdUser = userService.createUser(newUser);
            if (createdUser == null) {
                logger.severe("User creation failed - userService.createUser returned null");
                throw new Exception("Failed to create user account");
            }

            return createdUser;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in registerGoogleUser", e);
            throw new Exception("Google registration failed: " + e.getMessage(), e);
        }
    }
}