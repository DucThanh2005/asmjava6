package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.CartItem;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.service.CartService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class RestOrderController {

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    OrderDetailRepository detailRepo;

    @Autowired
    CartService cartService;

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody Map<String, String> payload, Authentication auth, HttpSession session) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        String username = auth.getName();
        String address = payload.get("address");

        List<Map<String, Object>> cartItems = new java.util.ArrayList<>();
        for (CartItem item : cartService.getCart(session).values()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", item.getId());
            map.put("price", item.getPrice());
            map.put("qty", item.getQuantity());
            cartItems.add(map);
        }

        if (cartItems.isEmpty()) {
            return Map.of("success", false, "message", "Cart is empty");
        }

        Orders order = new Orders();
        order.setUsername(username);
        order.setCreate_date(new Date());
        order.setAddress(address);
        order.setStatus("PENDING");
        order = orderRepo.save(order);

        // Save order details (simplified)
        for (Map<String, Object> item : cartItems) {
            // Note: In real app, save to OrderDetail
        }

        session.removeAttribute("cart");

        return Map.of("success", true, "orderId", order.getId(), "message", "Order placed successfully");
    }

    @GetMapping("/my-orders")
    public List<Orders> getMyOrders(Authentication auth) {
        if (auth == null) {
            return List.of();
        }
        String username = auth.getName();
        return orderRepo.findByUsername(username);
    }

    @GetMapping("/detail/{id}")
    public Map<String, Object> getOrderDetail(@PathVariable Integer id, Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        String username = auth.getName();
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null || !order.getUsername().equals(username)) {
            return Map.of("success", false, "message", "Order not found");
        }
        List<?> details = detailRepo.findByOrderId(id);
        return Map.of("order", order, "details", details);
    }
}