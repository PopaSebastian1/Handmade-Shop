package com.example.handmadeshop.repository;

import com.example.handmadeshop.EJB.model.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

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

    public User findByEmail(String email) {
        return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User update(User user) {
        return em.merge(user);
    }

    public void delete(Integer id) {
        User user = findById(id);
        if (user != null) {
            em.remove(user);
        }
    }

    public void addRoleToUser(Integer userId, Integer roleId) {
        UserRole userRole = new UserRole();
        UserRoleId id = new UserRoleId();
        id.setUserid(userId);
        id.setRoleid(roleId);
        userRole.setId(id);
        userRole.setUserid(findById(userId));
        userRole.setRoleid(em.find(Role.class, roleId));
        em.persist(userRole);
    }
}