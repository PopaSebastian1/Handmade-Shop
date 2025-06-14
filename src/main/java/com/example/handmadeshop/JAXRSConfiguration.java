package com.example.handmadeshop;

import com.example.handmadeshop.controllers.*;
import com.example.handmadeshop.security.JWTAuthFilter;
import com.example.handmadeshop.security.RoleAuthorizationFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("api") 
public class JAXRSConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                CorsFilter.class,       
                OptionsResource.class,
                ConfigController.class,
                UserController.class,
                ProductController.class,
                RoleController.class,
                GoogleAuthController.class,
                JWTAuthFilter.class,
                RoleAuthorizationFilter.class
        );
    }
}