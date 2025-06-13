package com.example.handmadeshop.DTO;

import com.example.handmadeshop.EJB.model.*;
import com.example.handmadeshop.services.KmsEncryptionService;

import java.util.stream.Collectors;

public class ModeMapper {

    private static final KmsEncryptionService kmsEncryptionService = new KmsEncryptionService();

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());

        dto.setName(kmsEncryptionService.decrypt(user.getName()));
        dto.setSurname(kmsEncryptionService.decrypt(user.getSurname()));
        dto.setEmail(kmsEncryptionService.decrypt(user.getEmail()));
        dto.setPassword(kmsEncryptionService.decrypt(user.getPassword()));

        if (user.getClientid() != null) {
            dto.setClientId(kmsEncryptionService.decrypt(user.getClientid()));
        }
        if (user.getClientsecret() != null) {
            dto.setClientSecret(kmsEncryptionService.decrypt(user.getClientsecret()));
        }

        if (user.getUserRoles() != null) {
            dto.setRoles(user.getUserRoles().stream()
                    .map(ur -> ur.getRoleid().getName())
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());

        user.setName(kmsEncryptionService.encrypt(userDTO.getName()));
        user.setSurname(kmsEncryptionService.encrypt(userDTO.getSurname()));
        user.setEmail(kmsEncryptionService.encrypt(userDTO.getEmail()));
        user.setPassword(kmsEncryptionService.encrypt(userDTO.getPassword()));

        if (userDTO.getClientId() != null) {
            user.setClientid(kmsEncryptionService.encrypt(userDTO.getClientId()));
        }
        if (userDTO.getClientSecret() != null) {
            user.setClientsecret(kmsEncryptionService.encrypt(userDTO.getClientSecret()));
        }

        return user;
    }

    public static RoleDTO toRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    public static Role toRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        return role;
    }

    public static UserProductDTO toUserProductDTO(UserProduct userProduct) {
        UserProductDTO dto = new UserProductDTO();
        dto.setUserId(userProduct.getUserid().getId());
        dto.setProductId(userProduct.getProductid().getId());
        dto.setQuantity(userProduct.getQuantity());
        return dto;
    }

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
}