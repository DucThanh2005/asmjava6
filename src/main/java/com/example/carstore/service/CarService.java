package com.example.carstore.service;

import java.util.List;

import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carstore.entity.Car;
import com.example.carstore.repository.CarRepository;

@Service
public class CarService {

    @Autowired
    CarRepository repo;

    public List<Car> findAll() {
        return repo.findAll();
    }

    public List<Car> findAllFiltered(String q) {
        if (!StringUtils.hasText(q)) {
            return repo.findAll();
        }
        return repo.findByNameContainingIgnoreCase(q.trim());
    }

    public Car findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public Car save(Car car) {
        return repo.save(car);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}