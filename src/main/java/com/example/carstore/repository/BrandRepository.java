package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carstore.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    
}
