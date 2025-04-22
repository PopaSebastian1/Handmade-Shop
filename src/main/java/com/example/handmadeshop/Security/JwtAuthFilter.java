package com.example.handmadeshop.Security;

import com.example.handmadeshop.Security.Secured;
import com.example.handmadeshop.Utility.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {
    private static final Logger logger = Logger.getLogger(JwtAuthFilter.class.getName());
    private static final String BEARER_PREFIX = "Bearer ";

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get required roles from the @Secured annotation
        Secured secured = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }

        List<String> requiredRoles = Arrays.asList(secured.value());

        // Get the Authorization header
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing or invalid Authorization header").build());
            return;
        }

        // Extract token
        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token
        if (jwtUtil.validateToken(token) == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid token").build());
            return;
        }

        // Check roles if specified
        if (!requiredRoles.isEmpty()) {
            List<String> userRoles = jwtUtil.getRolesFromToken(token);

            boolean hasRequiredRole = userRoles.stream()
                    .anyMatch(requiredRoles::contains);

            if (!hasRequiredRole) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Insufficient permissions").build());
                return;
            }
        }

        // Add user ID to the request context for resource methods
        requestContext.setProperty("userId", jwtUtil.getUserIdFromToken(token));
    }
}