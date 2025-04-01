package com.example.handmadeshop.repository;

import com.example.handmadeshop.DTO.ModeMapper;
import com.example.handmadeshop.DTO.UserDTO;
import com.example.handmadeshop.EJB.model.Product;
import com.example.handmadeshop.EJB.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserRepository {
    @PersistenceContext(unitName = "handmadePU")
    private EntityManager em;

    public void create(User user) {
        em.persist(user);
    }

    public User findById(Integer id) {
        return em.find(User.class, id);
    }

    public UserDTO findDTOById(Integer id) {
        User user = findById(id);
        return user != null ? ModeMapper.toUserDTO(user) : null;
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public List<UserDTO> findAllDTOs() {
        return findAll().stream()
                .map(ModeMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    public User update(User user) {
        return em.merge(user);
    }

    public void delete(Integer id) {
        User user = findById(id);
        if (user != null) {
            // Remove associations first
            user.getProducts().forEach(product -> product.getUsers().remove(user));
            em.remove(user);
        }
    }

    public void addProductToUser(Integer userId, Integer productId) {
        User user = findById(userId);
        if (user != null) {
            Product product = em.find(Product.class, productId);
            if (product != null) {
                user.addProduct(product);
                em.merge(user);
            }
        }
    }
}