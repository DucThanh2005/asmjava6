package com.example.carstore.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.carstore.repository.CarRepository;
import com.example.carstore.entity.Car;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestCarController {

    @Autowired
    CarRepository repo;

    // GET all cars
    @GetMapping
    public List<Car> getAll() {
        return repo.findAll();
    }

    // GET car by ID
    @GetMapping("/{id}")
    public Map<String, Object> getCarById(@PathVariable Integer id) {
        Car car = repo.findById(id).orElse(null);
        if (car == null) {
            return Map.of("success", false, "message", "Car not found");
        }
        return Map.of("success", true, "data", car);
    }

    // SEARCH cars by name
    @GetMapping("/search")
    public Map<String, Object> searchCars(
            @RequestParam String keyword) {

        try {

            List<Car> cars = repo.findByNameContainingIgnoreCase(keyword);

            return Map.of(
                    "success", true,
                    "data", cars,
                    "count", cars.size());

        } catch (Exception e) {

            return Map.of(
                    "success", false,
                    "message", e.getMessage());
        }
    }

    // CREATE car
    @PostMapping
    public Map<String, Object> create(@RequestBody Car car) {

        try {

            if (car.getName() == null || car.getName().trim().isEmpty()) {
                return Map.of(
                        "success", false,
                        "message", "Car name is required");
            }

            if (car.getPrice() == null || car.getPrice() <= 0) {
                return Map.of(
                        "success", false,
                        "message", "Invalid car price");
            }

            Car saved = repo.save(car);

            return Map.of(
                    "success", true,
                    "message", "Car created successfully",
                    "data", saved);

        } catch (Exception e) {

            return Map.of(
                    "success", false,
                    "message", "Error creating car: " + e.getMessage());
        }
    }

    // UPDATE car
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Integer id, @RequestBody Car car) {
        try {
            Car existing = repo.findById(id).orElse(null);
            if (existing == null) {
                return Map.of("success", false, "message", "Car not found");
            }

            if (car.getName() != null) {
                existing.setName(car.getName());
            }
            if (car.getPrice() != null) {
                existing.setPrice(car.getPrice());
            }
            if (car.getImage() != null) {
                existing.setImage(car.getImage());
            }
            if (car.getDescription() != null) {
                existing.setDescription(car.getDescription());
            }
            if (car.getBrandId() != null) {
                existing.setBrandId(car.getBrandId());
            }
            if (car.getYear() != null) {
                existing.setYear(car.getYear());
            }
            if (car.getColor() != null) {
                existing.setColor(car.getColor());
            }

            Car updated = repo.save(existing);
            return Map.of("success", true, "message", "Car updated successfully", "data", updated);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating car: " + e.getMessage());
        }
    }

    // DELETE car
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        try {
            if (!repo.existsById(id)) {
                return Map.of("success", false, "message", "Car not found");
            }
            repo.deleteById(id);
            return Map.of("success", true, "message", "Car deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting car: " + e.getMessage());
        }
    }

    // GET cars count
    @GetMapping("/stats/count")
    public Map<String, Object> getCarsCount() {
        try {
            long count = repo.count();
            return Map.of("success", true, "count", count);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error counting cars: " + e.getMessage());
        }
    }
}