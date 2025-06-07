package com.example.handmadeshop;

import com.example.handmadeshop.security.JWTAuthFilter;
import com.example.handmadeshop.security.RoleAuthorizationFilter;
import com.example.handmadeshop.controllers.GoogleAuthController;
import com.example.handmadeshop.controllers.ProductController;
import com.example.handmadeshop.controllers.RoleController;
import com.example.handmadeshop.controllers.UserController;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("api") // Fără slash la început
public class JAXRSConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                CorsFilter.class,       // Filtru CORS
                OptionsResource.class,   // Endpoint OPTIONS
                UserController.class,
                ProductController.class,
                RoleController.class,
                GoogleAuthController.class,
                JWTAuthFilter.class,
                RoleAuthorizationFilter.class
        );
    }
}