package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.carstore.entity.CartItem;
import com.example.carstore.entity.Car;
import com.example.carstore.entity.Orders;
import com.example.carstore.entity.OrderDetail;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.service.CartService;
import com.example.carstore.service.CarService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestOrderController {

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    OrderDetailRepository detailRepo;

    @Autowired
    CartService cartService;

    @Autowired
    CarService carService;

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean canViewOrder(Orders order, Authentication auth) {
        return auth != null &&
                (order.getUsername().equals(auth.getName()) || isAdmin(auth));
    }

    @GetMapping
    public Map<String, Object> getAllOrders(Authentication auth) {
        try {
            if (auth == null) {
                return Map.of("success", false, "message", "Not authenticated");
            }

            List<Orders> orders;

            if (isAdmin(auth)) {
                orders = orderRepo.findAll();
            } else {
                orders = orderRepo.findByUsername(auth.getName());
            }

            return Map.of("success", true, "data", orders, "count", orders.size());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOrderById(@PathVariable Integer id, Authentication auth) {
        try {
            if (auth == null) {
                return Map.of("success", false, "message", "Not authenticated");
            }

            Orders order = orderRepo.findById(id).orElse(null);

            if (order == null) {
                return Map.of("success", false, "message", "Order not found");
            }

            if (!canViewOrder(order, auth)) {
                return Map.of("success", false, "message", "Unauthorized");
            }

            return Map.of("success", true, "data", order);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching order: " + e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(
            @RequestBody Map<String, String> payload,
            Authentication auth,
            HttpSession session) {

        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }

        try {
            String username = auth.getName();
            String address = payload.get("address");

            if (address == null || address.trim().isEmpty()) {
                return Map.of("success", false, "message", "Address is required");
            }

            List<Map<String, Object>> cartItems = new ArrayList<>();

            for (CartItem item : cartService.getCart(session).values()) {
                Map<String, Object> map = new HashMap<>();
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

            for (Map<String, Object> item : cartItems) {
                Integer carId = ((Number) item.get("id")).intValue();
                Car car = carService.findById(carId);

                if (car == null) {
                    return Map.of("success", false, "message", "Car not found: " + carId);
                }

                OrderDetail detail = new OrderDetail();
                detail.setOrderId(order.getId());
                detail.setCar(car);
                detail.setPrice(((Number) item.get("price")).doubleValue());
                detail.setQuantity(((Number) item.get("qty")).intValue());

                detailRepo.save(detail);
            }

            session.removeAttribute("cart");

            return Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "message", "Order placed successfully");

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error placing order: " + e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public Map<String, Object> getMyOrders(Authentication auth) {
        try {
            if (auth == null) {
                return Map.of("success", false, "message", "Not authenticated");
            }

            List<Orders> orders = orderRepo.findByUsername(auth.getName());

            return Map.of("success", true, "data", orders, "count", orders.size());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/details")
    public Map<String, Object> getOrderDetail(@PathVariable Integer id, Authentication auth) {
        try {
            if (auth == null) {
                return Map.of("success", false, "message", "Not authenticated");
            }

            Orders order = orderRepo.findById(id).orElse(null);

            if (order == null) {
                return Map.of("success", false, "message", "Order not found");
            }

            if (!canViewOrder(order, auth)) {
                return Map.of("success", false, "message", "Unauthorized");
            }

            List<OrderDetail> details = detailRepo.findByOrderId(id);

            return Map.of(
                    "success", true,
                    "order", order,
                    "details", details,
                    "itemCount", details.size());

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching order details: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload,
            Authentication auth) {

        if (!isAdmin(auth)) {
            return Map.of("success", false, "message", "Access denied");
        }

        try {
            Orders order = orderRepo.findById(id).orElse(null);

            if (order == null) {
                return Map.of("success", false, "message", "Order not found");
            }

            String status = payload.get("status");

            if (status == null || status.trim().isEmpty()) {
                return Map.of("success", false, "message", "Status is required");
            }

            order.setStatus(status);
            orderRepo.save(order);

            return Map.of("success", true, "message", "Order status updated successfully");

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating order: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(
            @PathVariable Integer id,
            Authentication auth) {

        if (!isAdmin(auth)) {
            return Map.of("success", false, "message", "Access denied");
        }

        try {
            if (!orderRepo.existsById(id)) {
                return Map.of("success", false, "message", "Order not found");
            }

            List<OrderDetail> details = detailRepo.findByOrderId(id);

            for (OrderDetail detail : details) {
                detailRepo.deleteById(detail.getId());
            }

            orderRepo.deleteById(id);

            return Map.of("success", true, "message", "Order deleted successfully");

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting order: " + e.getMessage());
        }
    }

    @GetMapping("/summary/{id}")
    public Map<String, Object> getOrderSummary(
            @PathVariable Integer id,
            Authentication auth) {

        try {
            if (auth == null) {
                return Map.of("success", false, "message", "Not authenticated");
            }

            Orders order = orderRepo.findById(id).orElse(null);

            if (order == null) {
                return Map.of("success", false, "message", "Order not found");
            }

            if (!canViewOrder(order, auth)) {
                return Map.of("success", false, "message", "Unauthorized");
            }

            List<OrderDetail> details = detailRepo.findByOrderId(id);

            double totalAmount = 0;
            int totalItems = 0;

            for (OrderDetail detail : details) {
                totalAmount += detail.getPrice() * detail.getQuantity();
                totalItems += detail.getQuantity();
            }

            return Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "username", order.getUsername(),
                    "address", order.getAddress(),
                    "status", order.getStatus(),
                    "createDate", order.getCreate_date(),
                    "totalItems", totalItems,
                    "totalAmount", totalAmount);

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching order summary: " + e.getMessage());
        }
    }
}