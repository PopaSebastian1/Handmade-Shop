package EJB.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"UserProducts\"")
public class UserProduct {
    @Id
    @Column(name = "\"Id\"", nullable = false)
    private Integer id;

    @Column(name = "\"UserId\"", nullable = false)
    private Integer userId;

    @Column(name = "\"ProductId\"", nullable = false)
    private Integer productId;

    @Column(name = "\"Quantity\"", nullable = false)
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}