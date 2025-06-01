package com.example.handmadeshop.repository;

import com.example.handmadeshop.EJB.model.Role;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RoleRepository {
    @PersistenceContext(unitName = "handmadePU")
    private EntityManager em;

    public Role findById(Integer id) {
        return em.find(Role.class, id);
    }

    public Role findByName(String name) {
        return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public List<Role> findAll() {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }

    public void create(Role role) {
        em.persist(role);
    }

    public Role update(Role role) {
        return em.merge(role);
    }

    public void delete(Integer id) {
        Role role = em.find(Role.class, id);
        if (role != null) {
            em.remove(role);
        }
    }
}