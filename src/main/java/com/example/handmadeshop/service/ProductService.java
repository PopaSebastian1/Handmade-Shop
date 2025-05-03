package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.EJB.model.Product;
import com.example.handmadeshop.EJB.model.User;
import com.example.handmadeshop.EJB.model.UserProduct;
import com.example.handmadeshop.EJB.model.UserProductId;
import com.example.handmadeshop.repository.ProductRepository;
import com.example.handmadeshop.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class ProductService {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private UserRepository userRepository;

    @Transactional
    public String updateUserProductAssociation(Integer productId, Integer userId, Integer newQuantity) {
        Product product = productRepository.findById(productId);
        User user = userRepository.findById(userId);
        UserProduct existingAssociation = productRepository.findUserProductAssociation(userId, productId);

        // 🔁 Înlocuiește excepția cu return false
        if (existingAssociation != null && existingAssociation.getQuantity() == -1) {
            return "seller"; // indică faptul că e seller
        }

        int oldQuantity = existingAssociation != null ? existingAssociation.getQuantity() : 0;
        int quantityDifference = newQuantity - oldQuantity;

        if (newQuantity > 0 && product.getQuantity() < quantityDifference) {
            return "empty";
        }

        if (newQuantity == 0) {
            if (existingAssociation != null) {
                productRepository.deleteUserProduct(existingAssociation);
            }
        } else {
            if (existingAssociation != null) {
                existingAssociation.setQuantity(newQuantity);
                productRepository.updateUserProduct(existingAssociation);
            } else {
                UserProduct newAssociation = new UserProduct();
                UserProductId id = new UserProductId();
                id.setProductid(productId);
                id.setUserid(userId);
                newAssociation.setId(id);
                newAssociation.setProductid(product);
                newAssociation.setUserid(user);
                newAssociation.setQuantity(newQuantity);
                productRepository.persistUserProduct(newAssociation);
            }
        }

        if (oldQuantity > 0 || newQuantity > 0) {
            product.setQuantity(product.getQuantity() - quantityDifference);
            productRepository.update(product);
        }

        return "";
    }


    public List<ProductDTO> getProductsWithUserQuantities(Integer userId) {
        List<Object[]> results = productRepository.findProductsWithUserQuantities(userId);

        return results.stream()
                .map(result -> {
                    Product product = (Product) result[0];
                    Integer userQuantity = (Integer) result[1];

                    ProductDTO dto = ModeMapper.toProductDTO(product);
                    // Suprascriem quantity-ul din produs cu cel din asociere
                    dto.setQuantity(userQuantity);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsBySeller(Integer sellerId) {
        return productRepository.findBySeller(sellerId).stream()
                .map(ModeMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        try {
            Product product = ModeMapper.toProduct(productDTO);
            Product persistedProduct = productRepository.create(product);

            if (persistedProduct.getId() == null) {
                throw new IllegalStateException("Product ID was not generated after persistence");
            }

            return ModeMapper.toProductDTO(persistedProduct);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean checkExistingProductForSeller(Integer sellerId, String name, String image, BigDecimal price) {
        List<Product> existingProducts = productRepository.findBySeller(sellerId);

        return existingProducts.stream()
                .anyMatch(p ->
                        p.getName().equalsIgnoreCase(name) &&
                                Objects.equals(p.getImage(), image) &&
                                p.getPrice().compareTo(price) == 0
                );
    }

    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id);
        return product != null ? ModeMapper.toProductDTO(product) : null;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ModeMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO updateProduct(Integer id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id);
        if (existingProduct != null) {
            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setQuantity(productDTO.getQuantity());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setRating(productDTO.getRating());
            existingProduct.setImage(productDTO.getImage());

            Product updatedProduct = productRepository.update(existingProduct);
            return ModeMapper.toProductDTO(updatedProduct);
        }
        return null;
    }

    public void deleteProduct(Integer id) {
        productRepository.delete(id);
    }

    public void addUserToProduct(Integer productId, Integer userId, Integer quantity) {
        if (productId == null || userId == null) {
            throw new IllegalArgumentException("Product ID and User ID cannot be null");
        }

        Product product = productRepository.findById(productId);
        User user = userRepository.findById(userId);

        if (product == null || user == null) {
            throw new IllegalArgumentException("Product or User not found");
        }

        productRepository.addUserToProduct(productId, userId, quantity);
    }

    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .map(ModeMapper::toProductDTO)
                .collect(Collectors.toList());
    }
}