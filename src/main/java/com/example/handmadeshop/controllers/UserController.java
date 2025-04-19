package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.EJB.model.User;
import com.example.handmadeshop.repository.UserRepository;
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

    @POST
    public Response createUser(UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Integer id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public Response getAllUsers() {
        return Response.ok(userService.getAllUsers()).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Integer id, UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        if (updatedUser != null) {
            return Response.ok(updatedUser).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
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

        List<User> allUsers = userRepository.findAll();
        User matchedUser = null;

        for (User user : allUsers) {
            if (email.equals(email)) {
                matchedUser = user;
                break;
            }
        }

        if (matchedUser != null && matchedUser.getPassword().equals(password)) {
            UserDTO userDTO = ModeMapper.toUserDTO(matchedUser);
            userDTO.setPassword(null);
            return Response.ok(userDTO).build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Invalid email or password\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}