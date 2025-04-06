package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Inject
    private UserService userService;

    @POST
    public Response createUser(UserDTO userDTO) {
        logger.info("Creating user: " + userDTO.getEmail());
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();
        } catch (Exception e) {
            logger.severe("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user: " + e.getMessage())
                    .build();
        }

    }

    @GET
    public Response getAllUsers() {
        logger.info("Fetching all users");
        try {
            List<UserDTO> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.severe("Error fetching users: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching users")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") int id) {
        logger.info("Fetching user with ID: " + id);
        try {
            UserDTO user = userService.getUserById(id);
            if (user != null) {
                return Response.ok(user).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error fetching user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching user")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") int id, UserDTO userDTO) {
        logger.info("Updating user with ID: " + id);
        try {
            userDTO.setId(id);
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            if (updatedUser != null) {
                return Response.ok(updatedUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("User not found")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) {
        logger.info("Deleting user with ID: " + id);
        try {
            userService.deleteUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user")
                    .build();
        }
    }

    @POST
    @Path("/{userId}/products/{productId}")
    public Response addProductToUser(
            @PathParam("userId") int userId,
            @PathParam("productId") int productId) {
        logger.info("Adding product " + productId + " to user " + userId);
        try {
            userService.addProductToUser(userId, productId);
            return Response.ok().build();
        } catch (Exception e) {
            logger.severe("Error adding product to user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding product to user")
                    .build();
        }
    }
}