package EJB.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Users\"")
public class User {
    @Id
    @Column(name = "\"Id\"", nullable = false)
    private Integer id;

    @Lob
    @Column(name = "\"Name\"", nullable = false)
    private String name;

    @Lob
    @Column(name = "\"Password\"", nullable = false)
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}