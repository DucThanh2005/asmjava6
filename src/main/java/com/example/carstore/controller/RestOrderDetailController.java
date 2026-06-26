package com.example.carstore.controller;

import com.example.carstore.entity.Car;
import com.example.carstore.entity.OrderDetail;
import com.example.carstore.repository.CarRepository;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.util.ResponseUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order-details")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestOrderDetailController {

    private final OrderDetailRepository detailRepo;
    private final CarRepository carRepo;

    public RestOrderDetailController(OrderDetailRepository detailRepo, CarRepository carRepo) {
        this.detailRepo = detailRepo;
        this.carRepo = carRepo;
    }

    @GetMapping
    public List<OrderDetail> getAllOrderDetails() {
        return detailRepo.findAll();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOrderDetailById(@PathVariable Integer id) {
        return detailRepo.findById(id)
                .map(detail -> Map.<String, Object>of("success", true, "data", detail))
                .orElse(ResponseUtils.fail("Order detail not found"));
    }

    @GetMapping("/order/{orderId}")
    public Map<String, Object> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        return Map.of("success", true, "data", detailRepo.findByOrderId(orderId));
    }

    @PostMapping
    public Map<String, Object> createOrderDetail(@RequestBody OrderDetail detail) {
        String validation = attachCar(detail);
        if (validation != null) {
            return ResponseUtils.fail(validation);
        }
        return ResponseUtils.ok("Order detail created successfully", "data", detailRepo.save(detail));
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateOrderDetail(@PathVariable Integer id, @RequestBody OrderDetail detail) {
        return detailRepo.findById(id).map(existing -> {
            if (detail.getQuantity() != null) existing.setQuantity(detail.getQuantity());
            if (detail.getPrice() != null) existing.setPrice(detail.getPrice());
            if (detail.getCar() != null && detail.getCar().getId() != null) {
                carRepo.findById(detail.getCar().getId()).ifPresent(existing::setCar);
            }
            return ResponseUtils.ok("Order detail updated successfully", "data", detailRepo.save(existing));
        }).orElse(ResponseUtils.fail("Order detail not found"));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrderDetail(@PathVariable Integer id) {
        if (!detailRepo.existsById(id)) {
            return ResponseUtils.fail("Order detail not found");
        }
        detailRepo.deleteById(id);
        return ResponseUtils.ok("Order detail deleted successfully");
    }

    private String attachCar(OrderDetail detail) {
        if (detail.getCar() == null || detail.getCar().getId() == null) {
            return null;
        }
        Car car = carRepo.findById(detail.getCar().getId()).orElse(null);
        if (car == null) {
            return "Car not found";
        }
        detail.setCar(car);
        return null;
    }
}
