package com.example.carstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carstore.entity.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {

    List<Car> findByNameContainingIgnoreCase(String name);
}