package com.example.handmadeshop.DTO;

import com.example.handmadeshop.EJB.model.*;
import com.example.handmadeshop.service.KmsEncryptionService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModeMapper {

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());

        List<String> roles = Optional.ofNullable(user.getUserRoles())
                .orElse(Collections.emptyList())
                .stream()
                .map(userRole -> {
                    if (userRole != null && userRole.getRoleid() != null) {
                        return userRole.getRoleid().getName();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dto.setRoles(roles);

        return dto;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        return user;
    }

    // Role conversions
    public static RoleDTO toRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName()); // presupunem că numele rolului nu e sensibil
        return dto;
    }

    public static Role toRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        return role;
    }

    // UserProduct conversions
    public static UserProductDTO toUserProductDTO(UserProduct userProduct) {
        UserProductDTO dto = new UserProductDTO();
        dto.setUserId(userProduct.getUserid().getId());
        dto.setProductId(userProduct.getProductid().getId());
        dto.setQuantity(userProduct.getQuantity());
        return dto;
    }

    // Product conversions
    public static ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());

        dto.setRating(product.getRating());
        dto.setImage(product.getImage()); // imaginea presupunem că nu e criptată

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
}
