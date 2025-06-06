package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.AuthResponseDTO;
import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.EJB.model.User;
import com.example.handmadeshop.Security.Autenticated;
import com.example.handmadeshop.repository.UserRepository;
import com.example.handmadeshop.service.AuthenticationService;
import com.example.handmadeshop.service.KmsEncryptionService;
import com.example.handmadeshop.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private AuthenticationService authenticationService;

    @POST
    public Response createUser(UserDTO userDTO) {
        List<UserDTO> allUsers = userService.getAllUsers();

        for (UserDTO user : allUsers) {
            if (user.getEmail().equals(userDTO.getEmail())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("existent user")
                        .build();
            }
        }

        UserDTO createdUser = userService.createUser(userDTO);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }

    @GET
    @Path("/{id}")
    @Autenticated
    public Response getUserById(@PathParam("id") Integer id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Autenticated
    public Response getAllUsers() {
        return Response.ok(userService.getAllUsers()).build();
    }

    @PUT
    @Path("/{id}")
    @Autenticated
    public Response updateUser(@PathParam("id") Integer id, UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        if (updatedUser != null) {
            return Response.ok(updatedUser).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @Autenticated
    public Response deleteUser(@PathParam("id") Integer id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email,
                          @QueryParam("password") String password) {

        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Email and password are required\"}")
                    .build();
        }

        String authResponse = authenticationService.authenticate(email, password);

        if (authResponse != null) {
            return Response.ok(authResponse).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"Invalid email or password\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{userId}/roles")
    @Autenticated
    public Response updateUserRoles(
            @PathParam("userId") Integer userId,
            List<String> roleNames
    ) {
        UserDTO updated = userService.updateUserRoles(userId, roleNames);
        String newToken = authenticationService.authenticate(updated.getEmail(), updated.getPassword());

        return Response.ok(newToken).build();
    }
}