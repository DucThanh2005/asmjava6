package com.example.carstore.entity;

import javax.persistence.*;

@Entity
@Table(name = "orderdetail")
public class OrderDetail {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "order_id")
    private Integer orderId;

    // Giữ nguyên phần liên kết đối tượng Car đã sửa
    @ManyToOne
    @JoinColumn(name = "car_id") 
    private Car car;

    private Double price;
    private Integer quantity;

    // Constructor không tham số (Bắt buộc phải có trong JPA)
    public OrderDetail() {}

    // Constructor đầy đủ tham số
    public OrderDetail(Integer id, Integer orderId, Car car, Double price, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.car = car;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter và Setter cho Car (Quan trọng nhất để Repository gọi được d.car.name)
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    // Các Getter và Setter còn lại
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