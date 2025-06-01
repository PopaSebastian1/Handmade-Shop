package com.example.handmadeshop.repository;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.ProductDTO;
import com.example.handmadeshop.EJB.model.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProductRepository {
    @PersistenceContext(unitName = "handmadePU")
    private EntityManager em;

    public List<Object[]> findProductsWithUserQuantities(Integer userId) {
        return em.createQuery(
                        "SELECT p, up.quantity FROM Product p " +
                                "JOIN p.userProducts up " +
                                "WHERE up.userid.id = :userId AND up.quantity > 0",
                        Object[].class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public UserProduct findUserProductAssociation(Integer userId, Integer productId) {
        try {
            return em.createQuery(
                            "SELECT up FROM UserProduct up " +
                                    "WHERE up.userid.id = :userId AND " +
                                    "up.productid.id = :productId",
                            UserProduct.class)
                    .setParameter("userId", userId)
                    .setParameter("productId", productId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteUserProduct(UserProduct userProduct) {
        em.remove(userProduct);
    }

    public void updateUserProduct(UserProduct userProduct) {
        em.merge(userProduct);
    }

    public void persistUserProduct(UserProduct userProduct) {
        em.persist(userProduct);
    }

    public List<Product> findBySeller(Integer sellerId) {
        return em.createQuery(
                        "SELECT DISTINCT p FROM Product p " +
                                "JOIN p.userProducts up " +
                                "WHERE up.userid.id = :sellerId AND up.quantity = -1",
                        Product.class)
                .setParameter("sellerId", sellerId)
                .getResultList();
    }

    public Product create(Product product) {
        em.persist(product);
        em.flush(); // Forțează sincronizarea cu baza de date pentru a obține ID-ul
        return product;
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
        // Verificăm existența entităților
        Product product = em.find(Product.class, productId);
        User user = em.find(User.class, userId);

        if (product == null || user == null) {
            throw new IllegalArgumentException("Product or User not found");
        }

        // Cream ID-ul compus
        UserProductId id = new UserProductId();
        id.setProductid(productId);
        id.setUserid(userId);

        // Cream asocierea
        UserProduct userProduct = new UserProduct();
        userProduct.setId(id);
        userProduct.setProductid(product);
        userProduct.setUserid(user);
        userProduct.setQuantity(quantity);

        em.persist(userProduct);
    }
}