package com.example.handmadeshop.EJB.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"UserRole\"")
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    @MapsId("userid")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @MapsId("roleid")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "roleid", nullable = false)
    private Role roleid;

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Role getRoleid() {
        return roleid;
    }

    public void setRoleid(Role roleid) {
        this.roleid = roleid;
    }

}