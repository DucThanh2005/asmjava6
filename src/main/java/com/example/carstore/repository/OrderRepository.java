package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carstore.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
}