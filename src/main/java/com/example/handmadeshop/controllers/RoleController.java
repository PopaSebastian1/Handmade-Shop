package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.RoleDTO;
import com.example.handmadeshop.service.RoleService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleController {

    @Inject
    private RoleService roleService;

    @POST
    public Response createRole(RoleDTO roleDTO) {
        try {
            RoleDTO createdRole = roleService.createRole(roleDTO);
            return Response
                    .status(Response.Status.CREATED)
                    .entity(createdRole)
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Error creating role: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getRoleById(@PathParam("id") Integer id) {
        RoleDTO role = roleService.getRoleById(id);
        if (role != null) {
            return Response.ok(role).build();
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Role with id " + id + " not found")
                .build();
    }

    @GET
    public Response getAllRoles() {
        return Response
                .ok(roleService.getAllRoles())
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateRole(@PathParam("id") Integer id, RoleDTO roleDTO) {
        try {
            roleDTO.setId(id);
            RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
            if (updatedRole != null) {
                return Response.ok(updatedRole).build();
            }
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Role not found")
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Error updating role: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") Integer id) {
        try {
            roleService.deleteRole(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Error deleting role: " + e.getMessage())
                    .build();
        }
    }
}