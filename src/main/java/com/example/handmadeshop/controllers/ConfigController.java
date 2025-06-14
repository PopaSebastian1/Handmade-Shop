package com.example.handmadeshop.controllers;

import com.example.handmadeshop.services.SecretsManagerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigController {

    @GET
    public Response getConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("lambdaKey", SecretsManagerService.get("lambdaKey"));

            return Response.ok(config).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to load configuration\"}")
                    .build();
        }
    }
}