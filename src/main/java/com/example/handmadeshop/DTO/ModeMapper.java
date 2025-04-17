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
        KmsEncryptionService kmsService = new KmsEncryptionService();
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());

        if (user.getName() != null)
            dto.setName(kmsService.decrypt(user.getName()));

        if (user.getSurname() != null)
            dto.setSurname(kmsService.decrypt(user.getSurname()));

        if (user.getEmail() != null)
            dto.setEmail(kmsService.decrypt(user.getEmail()));

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
        KmsEncryptionService kmsService = new KmsEncryptionService();
        User user = new User();
        user.setId(userDTO.getId());

        if (userDTO.getName() != null)
            user.setName(kmsService.encrypt(userDTO.getName()));

        if (userDTO.getSurname() != null)
            user.setSurname(kmsService.encrypt(userDTO.getSurname()));

        if (userDTO.getEmail() != null)
            user.setEmail(kmsService.encrypt(userDTO.getEmail()));

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(kmsService.encrypt(userDTO.getPassword()));
        }
        user.setClientid(userDTO.getClientId());

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
        KmsEncryptionService kmsService = new KmsEncryptionService();
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());

        if (product.getName() != null)
            dto.setName(kmsService.decrypt(product.getName()));

        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());

        if (product.getDescription() != null)
            dto.setDescription(kmsService.decrypt(product.getDescription()));

        dto.setRating(product.getRating());
        dto.setImage(product.getImage()); // imaginea presupunem că nu e criptată

        return dto;
    }

    public static Product toProduct(ProductDTO productDTO) {
        KmsEncryptionService kmsService = new KmsEncryptionService();
        Product product = new Product();
        product.setId(productDTO.getId());

        if (productDTO.getName() != null)
            product.setName(kmsService.encrypt(productDTO.getName()));

        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        if (productDTO.getDescription() != null)
            product.setDescription(kmsService.encrypt(productDTO.getDescription()));

        product.setRating(productDTO.getRating());
        product.setImage(productDTO.getImage());

        return product;
    }
}
