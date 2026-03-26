package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.Account;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.AccountRepository;
import com.example.carstore.repository.OrderRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class RestAdminController {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    OrderRepository orderRepo;

    // Users management
    @GetMapping("/users")
    public List<Account> getUsers() {
        return accountRepo.findAll();
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Account account) {
        if (accountRepo.existsById(account.getUsername())) {
            return Map.of("success", false, "message", "Username already exists");
        }
        accountRepo.save(account);
        return Map.of("success", true, "message", "User created successfully");
    }

    @PutMapping("/users/{username}")
    public Map<String, Object> updateUser(@PathVariable String username, @RequestBody Account account) {
        Account existing = accountRepo.findById(username).orElse(null);
        if (existing == null) {
            return Map.of("success", false, "message", "User not found");
        }
        existing.setFullname(account.getFullname());
        existing.setEmail(account.getEmail());
        existing.setRole(account.getRole());
        accountRepo.save(existing);
        return Map.of("success", true, "message", "User updated successfully");
    }

    @DeleteMapping("/users/{username}")
    public Map<String, Object> deleteUser(@PathVariable String username) {
        if (accountRepo.existsById(username)) {
            accountRepo.deleteById(username);
            return Map.of("success", true, "message", "User deleted successfully");
        }
        return Map.of("success", false, "message", "User not found");
    }

    // Orders management
    @GetMapping("/orders")
    public List<Orders> getOrders() {
        return orderRepo.findAll();
    }

    @PutMapping("/orders/{id}/status")
    public Map<String, Object> updateOrderStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            return Map.of("success", false, "message", "Order not found");
        }
        order.setStatus(payload.get("status"));
        orderRepo.save(order);
        return Map.of("success", true, "message", "Order status updated successfully");
    }

    @DeleteMapping("/orders/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Integer id) {
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
            return Map.of("success", true, "message", "Order deleted successfully");
        }
        return Map.of("success", false, "message", "Order not found");
    }

    // Dashboard stats
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long totalCars = 0; // Would need CarRepository.count()
        long totalUsers = accountRepo.count();
        long totalOrders = orderRepo.count();
        return Map.of("totalCars", totalCars, "totalUsers", totalUsers, "totalOrders", totalOrders);
    }
}