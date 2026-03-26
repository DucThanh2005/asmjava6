package com.example.carstore.entity;

import javax.persistence.*;

@Entity
@Table(name = "orderdetail")
public class OrderDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "order_id")
    Integer orderId;
    Integer car_id;
    Double price;
    Integer quantity;

    public OrderDetail() {}

    public OrderDetail(Integer id, Integer orderId, Integer car_id, Double price, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.car_id = car_id;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getCar_id() {
        return car_id;
    }

    public void setCar_id(Integer car_id) {
        this.car_id = car_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}