package com.example.handmadeshop;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok()
                    .header("Access-Control-Allow-Origin", "https://localhost:4200")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Max-Age", "1209600")
                    .build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Only add CORS headers for non-OPTIONS requests (OPTIONS are handled above)
        if (!"OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "https://localhost:4200");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }
}