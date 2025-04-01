package com.example.handmadeshop.repository;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.EJB.model.Product;
import com.example.handmadeshop.EJB.model.User;

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
            // Remove associations first
            product.getUsers().forEach(user -> user.getProducts().remove(product));
            em.remove(product);
        }
    }

    public void addUserToProduct(Integer productId, Integer userId) {
        Product product = findById(productId);
        if (product != null) {
            User user = em.find(User.class, userId);
            if (user != null) {
                product.addUser(user);
                em.merge(product);
            }
        }
    }
}