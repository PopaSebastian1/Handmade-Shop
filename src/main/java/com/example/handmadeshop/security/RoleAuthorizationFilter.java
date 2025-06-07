package com.example.handmadeshop.security;

import com.example.handmadeshop.Utility.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
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

@Provider
@RoleRequired("")
@Priority(200)
public class RoleAuthorizationFilter implements ContainerRequestFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get required roles from annotation (method or class)
        RoleRequired annotation = resourceInfo.getResourceMethod().getAnnotation(RoleRequired.class);
        if (annotation == null) {
            annotation = resourceInfo.getResourceClass().getAnnotation(RoleRequired.class);
        }
        String[] requiredRoles = annotation != null ? annotation.value() : new String[0];

        // Extract token from header
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortWithForbidden(requestContext, null, requiredRoles);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        try {
            boolean hasAnyRole = Arrays.stream(requiredRoles)
                    .anyMatch(role -> jwtUtil.hasRole(token, role));
            if (!hasAnyRole) {
                abortWithForbidden(requestContext, token, requiredRoles);
            }
        } catch (Exception e) {
            abortWithForbidden(requestContext, token, requiredRoles);
        }
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String token, String[] requiredRoles) {
        Claims claims = jwtUtil.getClaims(token);
        List<String> userRoles = claims != null ? claims.get("roles", List.class) : List.of();

        String debugInfo = String.format(
                "{\"error\":\"Access denied: Insufficient permissions\", " +
                        "\"requiredRoles\":%s, " +
                        "\"userRoles\":%s}",
                Arrays.toString(requiredRoles),
                userRoles
        );

        requestContext.abortWith(Response
                .status(Response.Status.FORBIDDEN)
                .entity(debugInfo)
                .build());
    }
}