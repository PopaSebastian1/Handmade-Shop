package com.example.handmadeshop.service;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.EJB.model.Product;
import com.example.handmadeshop.repository.ProductRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProductService {
    @Inject
    private ProductRepository productRepository;

    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = ModeMapper.toProduct(productDTO);
        productRepository.create(product);
        return ModeMapper.toProductDTO(product);
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