package com.example.handmadeshop.DTO;

import com.example.handmadeshop.EJB.model.Product;
import com.example.handmadeshop.EJB.model.User;

public class ModeMapper {

    // Product conversions
    public static ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setRating(product.getRating());
        dto.setImage(product.getImage());
        return dto;
    }

    public static Product toProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        product.setRating(productDTO.getRating());
        product.setImage(productDTO.getImage());
        return product;
    }

    // User conversions
    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // Note: We typically don't include password in DTO
        return dto;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // Only when creating/updating
        user.setRole(userDTO.getRole());
        return user;
    }
}