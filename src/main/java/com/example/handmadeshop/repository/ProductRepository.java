package com.example.handmadeshop.repository;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.EJB.model.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProductRepository {
    @PersistenceContext(unitName = "handmadePU")
    private EntityManager em;

    public void create(Product product) {
        em.persist(product);
    }

    public Product findById(Integer id) {
        return em.find(Product.class, id);
    }

    public ProductDTO findDTOById(Integer id) {
        Product product = findById(id);
        return product != null ? ModeMapper.toProductDTO(product) : null;
    }

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public List<ProductDTO> findAllDTOs() {
        return findAll().stream()
                .map(ModeMapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public Product update(Product product) {
        return em.merge(product);
    }

    public void delete(Integer id) {
        Product product = findById(id);
        if (product != null) {
            em.remove(product);
        }
    }

    public void addUserToProduct(Integer productId, Integer userId, Integer quantity) {
        Product product = findById(productId);
        User user = em.find(User.class, userId);

        if (product != null && user != null) {
            UserProduct userProduct = new UserProduct();
            UserProductId id = new UserProductId();
            id.setProductid(productId);
            id.setUserid(userId);
            userProduct.setId(id);
            userProduct.setProductid(product);
            userProduct.setUserid(user);
            userProduct.setQuantity(quantity);

            em.persist(userProduct);
        }
    }
}