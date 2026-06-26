package com.example.carstore.controller;

import com.example.carstore.entity.Car;
import com.example.carstore.repository.CarRepository;
import com.example.carstore.util.ResponseUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestCarController {

    private final CarRepository carRepo;

    public RestCarController(CarRepository carRepo) {
        this.carRepo = carRepo;
    }

    @GetMapping
    public List<Car> getAll() {
        return carRepo.findAll();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCarById(@PathVariable Integer id) {
        return carRepo.findById(id)
                .map(car -> Map.<String, Object>of("success", true, "data", car))
                .orElse(ResponseUtils.fail("Car not found"));
    }

    @GetMapping("/search")
    public Map<String, Object> searchCars(@RequestParam String keyword) {
        List<Car> cars = carRepo.findByNameContainingIgnoreCase(keyword);
        return Map.of("success", true, "data", cars, "count", cars.size());
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Car car) {
        String validation = validateCar(car);
        if (validation != null) {
            return ResponseUtils.fail(validation);
        }
        return ResponseUtils.ok("Car created successfully", "data", carRepo.save(car));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Integer id, @RequestBody Car car) {
        return carRepo.findById(id).map(existing -> {
            copyCarFields(car, existing);
            return ResponseUtils.ok("Car updated successfully", "data", carRepo.save(existing));
        }).orElse(ResponseUtils.fail("Car not found"));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        if (!carRepo.existsById(id)) {
            return ResponseUtils.fail("Car not found");
        }
        carRepo.deleteById(id);
        return ResponseUtils.ok("Car deleted successfully");
    }

    @GetMapping("/stats/count")
    public Map<String, Object> getCarsCount() {
        return Map.of("success", true, "count", carRepo.count());
    }

    private String validateCar(Car car) {
        if (car == null || car.getName() == null || car.getName().trim().isEmpty()) {
            return "Car name is required";
        }
        if (car.getPrice() == null || car.getPrice() <= 0) {
            return "Invalid car price";
        }
        return null;
    }

    private void copyCarFields(Car source, Car target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getPrice() != null) target.setPrice(source.getPrice());
        if (source.getImage() != null) target.setImage(source.getImage());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getBrandId() != null) target.setBrandId(source.getBrandId());
        if (source.getYear() != null) target.setYear(source.getYear());
        if (source.getColor() != null) target.setColor(source.getColor());
    }
}
