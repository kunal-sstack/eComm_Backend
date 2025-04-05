package com.projectX.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    private double orderCost;

    private String orderStatus;

    public double getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(double orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
