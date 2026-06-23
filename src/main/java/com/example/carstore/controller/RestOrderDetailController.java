package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.OrderDetail;
import com.example.carstore.entity.Car;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.repository.CarRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order-details")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestOrderDetailController {

    @Autowired
    OrderDetailRepository detailRepo;

    @Autowired
    CarRepository carRepo;

    // GET all order details
    @GetMapping
    public List<OrderDetail> getAllOrderDetails() {
        return detailRepo.findAll();
    }

    // GET order details by ID
    @GetMapping("/{id}")
    public Map<String, Object> getOrderDetailById(@PathVariable Integer id) {
        OrderDetail detail = detailRepo.findById(id).orElse(null);
        if (detail == null) {
            return Map.of("success", false, "message", "Order detail not found");
        }
        return Map.of("success", true, "data", detail);
    }

    // GET order details by order ID
    @GetMapping("/order/{orderId}")
    public Map<String, Object> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        try {
            List<OrderDetail> details = detailRepo.findByOrderId(orderId);
            return Map.of("success", true, "data", details);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching order details: " + e.getMessage());
        }
    }

    // CREATE order detail
    @PostMapping
    public Map<String, Object> createOrderDetail(@RequestBody OrderDetail detail) {
        try {
            // Validate car exists
            if (detail.getCar() != null && detail.getCar().getId() != null) {
                Car car = carRepo.findById(detail.getCar().getId()).orElse(null);
                if (car == null) {
                    return Map.of("success", false, "message", "Car not found");
                }
                detail.setCar(car);
            }
            OrderDetail saved = detailRepo.save(detail);
            return Map.of("success", true, "message", "Order detail created successfully", "data", saved);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating order detail: " + e.getMessage());
        }
    }

    // UPDATE order detail
    @PutMapping("/{id}")
    public Map<String, Object> updateOrderDetail(@PathVariable Integer id, @RequestBody OrderDetail detail) {
        OrderDetail existing = detailRepo.findById(id).orElse(null);
        if (existing == null) {
            return Map.of("success", false, "message", "Order detail not found");
        }

        if (detail.getQuantity() != null) {
            existing.setQuantity(detail.getQuantity());
        }
        if (detail.getPrice() != null) {
            existing.setPrice(detail.getPrice());
        }
        if (detail.getCar() != null && detail.getCar().getId() != null) {
            Car car = carRepo.findById(detail.getCar().getId()).orElse(null);
            if (car != null) {
                existing.setCar(car);
            }
        }

        OrderDetail updated = detailRepo.save(existing);
        return Map.of("success", true, "message", "Order detail updated successfully", "data", updated);
    }

    // DELETE order detail
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrderDetail(@PathVariable Integer id) {
        if (!detailRepo.existsById(id)) {
            return Map.of("success", false, "message", "Order detail not found");
        }
        detailRepo.deleteById(id);
        return Map.of("success", true, "message", "Order detail deleted successfully");
    }
}
