package com.example.handmadeshop;

import com.example.handmadeshop.Security.JwtAuthFilter;
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
                UserController.class,
                ProductController.class,
                RoleController.class,
                CorsFilter.class,
                GoogleAuthController.class,
                JwtAuthFilter.class
        );
    }
}