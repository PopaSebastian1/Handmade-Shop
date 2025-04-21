package com.example.handmadeshop.EJB.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "surname", nullable = false)
    private String surname;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255)
    @Column(name = "clientid")
    private String clientid;

    @Size(max = 255)
    @Column(name = "clientsecret")
    private String clientsecret;

    @OneToMany(mappedBy = "userid", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "userid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProduct> userProducts = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientsecret() {
        return clientsecret;
    }

    public void setClientsecret(String clientsecret) {
        this.clientsecret = clientsecret;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<UserProduct> getUserProducts() {
        return userProducts;
    }

    public void setUserProducts(List<UserProduct> userProducts) {
        this.userProducts = userProducts;
    }

    public void addRole(Role role) {
        if (userRoles.stream().noneMatch(ur -> ur.getRoleid().equals(role))) {
            UserRole userRole = new UserRole();
            UserRoleId id = new UserRoleId();
            id.setUserid(this.id);
            id.setRoleid(role.getId());
            userRole.setId(id);
            userRole.setUserid(this);
            userRole.setRoleid(role);
            this.userRoles.add(userRole);
        }
    }


    public void removeRole(Role role) {
        userRoles.removeIf(ur -> ur.getRoleid().equals(role));
    }

    public void addProduct(Product product, int quantity) {
        UserProduct userProduct = new UserProduct();
        UserProductId id = new UserProductId();
        id.setUserid(this.id);
        id.setProductid(product.getId());
        userProduct.setId(id);
        userProduct.setUserid(this);
        userProduct.setProductid(product);
        userProduct.setQuantity(quantity);
        this.userProducts.add(userProduct);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}