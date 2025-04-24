package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.Security.Autenticated;
import com.example.handmadeshop.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    private static final Logger logger = Logger.getLogger(ProductController.class.getName());

    @Inject
    private ProductService productService;

    @POST public Response createProduct(ProductDTO productDTO) {
        logger.info("Creating product: " + productDTO);
        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(createdProduct)
                    .build();
        } catch (Exception e) {
            logger.severe("Error creating product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating product")
                    .build();
        }
    }

    @GET
    //@Autenticated
    public Response getAllProducts() {
        logger.info("Fetching all products");
        try {
            List<ProductDTO> products = productService.getAllProducts();
            return Response.ok(products).build();
        } catch (Exception e) {
            logger.severe("Error fetching products: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching products")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") int id) {
        logger.info("Fetching product with ID: " + id);
        try {
            ProductDTO product = productService.getProductById(id);
            if (product != null) {
                return Response.ok(product).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error fetching product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching product")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") int id, ProductDTO productDTO) {
        logger.info("Updating product with ID: " + id);
        try {
            productDTO.setId(id);
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            if (updatedProduct != null) {
                return Response.ok(updatedProduct).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product not found")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Error updating product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating product")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") int id) {
        logger.info("Deleting product with ID: " + id);
        try {
            productService.deleteProduct(id);
            return Response.noContent().build();
        } catch (Exception e) {
            logger.severe("Error deleting product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting product")
                    .build();
        }
    }

    @POST
    @Path("/{productId}/users/{userId}")
    public Response addUserToProduct(
            @PathParam("productId") int productId,
            @PathParam("userId") int userId,
            @QueryParam("quantity") @DefaultValue("1") int quantity) {
        logger.info("Adding user " + userId + " to product " + productId + " with quantity " + quantity);
        try {
            productService.addUserToProduct(productId, userId, quantity);
            return Response.ok().build();
        } catch (Exception e) {
            logger.severe("Error adding user to product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding user to product")
                    .build();
        }
    }
}