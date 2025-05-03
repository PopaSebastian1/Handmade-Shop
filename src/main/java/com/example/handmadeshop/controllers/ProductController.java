package com.example.handmadeshop.controllers;

import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.Security.Autenticated;
import com.example.handmadeshop.service.ProductService;
import com.example.handmadeshop.service.UserService;
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
    @Inject
    private UserService userService;

    @GET
    @Path("/user/{userId}")
    public Response getProductsWithUserQuantities(@PathParam("userId") int userId) {
        logger.info("Fetching products with user quantities for user ID: " + userId);
        try {
            List<ProductDTO> products = productService.getProductsWithUserQuantities(userId);
            return Response.ok(products).build();
        } catch (Exception e) {
            logger.severe("Error fetching products with quantities: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error fetching products with quantities")
                    .build();
        }
    }

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

    @POST
    @Path("/{productId}/user/{userId}")
    public Response associateUserWithProduct(
            @PathParam("productId") int productId,
            @PathParam("userId") int userId,
            @QueryParam("quantity") int quantity) {

        logger.info(String.format("Modifying association: user %d with product %d, quantity: %d",
                userId, productId, quantity));

        try {
            // 1. Verificare specială pentru cantitatea -1 (seller)
            if (quantity == -1) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cannot modify seller-product association directly. Use the seller endpoint.")
                        .build();
            }

            // 2. Verificăm existența produsului și a userului
            ProductDTO product = productService.getProductById(productId);
            UserDTO user = userService.getUserById(userId);

            if (product == null || user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Product or User not found")
                        .build();
            }

            // 3. Procesăm asocierea
            productService.updateUserProductAssociation(productId, userId, quantity);

            return Response.status(Response.Status.OK)
                    .entity("Product association updated successfully")
                    .build();

        } catch (Exception e) {
            logger.severe("Error updating product association: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/seller/{sellerId}")
    public Response addProductForSale(
            @PathParam("sellerId") int sellerId,
            ProductDTO productDTO) {
        logger.info("Seller " + sellerId + " is adding product for sale: " + productDTO);

        try {
            // 1. Verificăm dacă produsul există deja pentru acest seller
            boolean productExists = productService.checkExistingProductForSeller(
                    sellerId,
                    productDTO.getName(),
                    productDTO.getImage(),
                    productDTO.getPrice()
            );

            if (productExists) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Product with the same name, image and price already exists for this seller")
                        .build();
            }

            // 2. Cream produsul cu cantitatea reală
            ProductDTO createdProduct = productService.createProduct(productDTO);

            // 3. Verificăm dacă produsul a fost creat cu succes și are ID
            if (createdProduct == null || createdProduct.getId() == null) {
                throw new IllegalArgumentException("Failed to create product or product ID is null");
            }

            // 4. Asociem produsul cu seller-ul cu quantity = -1
            productService.addUserToProduct(createdProduct.getId(), sellerId, -1);

            return Response.status(Response.Status.CREATED)
                    .entity(createdProduct)
                    .build();
        } catch (Exception e) {
            logger.severe("Error adding product for sale: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding product for sale: " + e.getMessage())
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