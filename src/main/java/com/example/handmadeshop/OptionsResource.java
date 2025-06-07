package com.example.handmadeshop;

import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/{path:.*}")
public class OptionsResource {
    @OPTIONS
    public Response handleOptions() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "https://localhost:4200")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }
}