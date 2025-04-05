package com.projectX.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double totalCartPrice;

    public Cart() {

    }

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<CartItem> ci = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getCi() {
        return ci;
    }

    public void setCi(List<CartItem> ci) {
        this.ci = ci;
    }

    public double getTotalCartPrice() {
        return totalCartPrice;
    }

    public void setTotalCartPrice(double totalCartPrice) {
        this.totalCartPrice = totalCartPrice;
    }
}
