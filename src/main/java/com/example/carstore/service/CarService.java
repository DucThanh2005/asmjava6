package com.example.carstore.service;

import com.example.carstore.entity.Car;
import com.example.carstore.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepo;

    public CarService(CarRepository carRepo) {
        this.carRepo = carRepo;
    }

    public List<Car> findAll() {
        return carRepo.findAll();
    }

    public List<Car> search(String keyword) {
        return findAllFiltered(keyword);
    }

    public List<Car> findAllFiltered(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return carRepo.findAll();
        }
        return carRepo.findByNameContainingIgnoreCase(keyword.trim());
    }

    public Car findById(Integer id) {
        return carRepo.findById(id).orElse(null);
    }

    public Car save(Car car) {
        return carRepo.save(car);
    }

    public void delete(Integer id) {
        carRepo.deleteById(id);
    }
}
