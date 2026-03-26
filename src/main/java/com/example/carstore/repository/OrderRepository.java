package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carstore.entity.Orders;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByUsername(String username);
}