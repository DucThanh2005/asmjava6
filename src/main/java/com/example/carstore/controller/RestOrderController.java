package com.example.carstore.controller;

import com.example.carstore.entity.OrderDetail;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.service.CartService;
import com.example.carstore.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestOrderController {

    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final CartService cartService;
    private final OrderService orderService;

    public RestOrderController(OrderRepository orderRepo,
                               OrderDetailRepository detailRepo,
                               CartService cartService,
                               OrderService orderService) {
        this.orderRepo = orderRepo;
        this.detailRepo = detailRepo;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities()
                .stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean canViewOrder(Orders order, Authentication auth) {
        return auth != null
                && order != null
                && (isAdmin(auth) || auth.getName().equals(order.getUsername()));
    }

    @GetMapping
    public Map<String, Object> getAllOrders(Authentication auth) {
        if (auth == null) {
            return fail("Not authenticated");
        }

        List<Orders> orders = isAdmin(auth)
                ? orderRepo.findAll()
                : orderRepo.findByUsername(auth.getName());

        return Map.of("success", true, "data", orders, "count", orders.size());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOrderById(@PathVariable Integer id, Authentication auth) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            return fail("Order not found");
        }
        if (!canViewOrder(order, auth)) {
            return fail(auth == null ? "Not authenticated" : "Unauthorized");
        }
        return Map.of("success", true, "data", order);
    }


    /**
     * Giữ lại API cũ POST /api/orders để không làm hỏng frontend cũ nếu còn gọi endpoint này.
     * Logic lưu đơn vẫn đi qua OrderService để có transaction và username thật từ Spring Security.
     */
    @PostMapping
    public Map<String, Object> createFromCartPayload(@RequestBody List<Map<String, Object>> cartPayload,
                                                     Authentication auth,
                                                     HttpSession session) {
        if (auth == null) {
            return fail("Not authenticated");
        }
        if (cartPayload == null || cartPayload.isEmpty()) {
            return fail("Cart is empty");
        }

        try {
            Map<Integer, com.example.carstore.entity.CartItem> cart = new HashMap<>();
            for (Map<String, Object> item : cartPayload) {
                Integer carId = Integer.parseInt(item.get("id").toString());
                double price = Double.parseDouble(item.get("price").toString());
                Object qtyValue = item.containsKey("qty") ? item.get("qty") : item.get("quantity");
                int quantity = Integer.parseInt(qtyValue.toString());
                cart.put(carId, new com.example.carstore.entity.CartItem(carId, null, price, quantity));
            }

            Orders order = orderService.checkout(auth.getName(), "Đặt hàng từ API /api/orders", cart);
            return Map.of("success", true, "data", order, "orderId", order.getId());
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public Map<String, Object> checkout(@RequestBody Map<String, String> payload,
                                        Authentication auth,
                                        HttpSession session) {
        if (auth == null) {
            return fail("Not authenticated");
        }

        try {
            String address = payload == null ? null : payload.get("address");
            Orders order = orderService.checkout(auth.getName(), address, cartService.getCart(session));
            cartService.clear(session);

            return Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "message", "Order placed successfully");
        } catch (IllegalArgumentException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("Error placing order: " + e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public Map<String, Object> getMyOrders(Authentication auth) {
        if (auth == null) {
            return fail("Not authenticated");
        }

        List<Orders> orders = orderRepo.findByUsername(auth.getName());
        return Map.of("success", true, "data", orders, "count", orders.size());
    }

    @GetMapping("/{id}/details")
    public Map<String, Object> getOrderDetail(@PathVariable Integer id, Authentication auth) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            return fail("Order not found");
        }
        if (!canViewOrder(order, auth)) {
            return fail(auth == null ? "Not authenticated" : "Unauthorized");
        }

        List<OrderDetail> details = detailRepo.findByOrderId(id);
        return Map.of(
                "success", true,
                "order", order,
                "details", details,
                "itemCount", details.size());
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateOrderStatus(@PathVariable Integer id,
                                                 @RequestBody Map<String, String> payload,
                                                 Authentication auth) {
        if (!isAdmin(auth)) {
            return fail("Access denied");
        }

        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            return fail("Order not found");
        }

        String status = payload == null ? null : payload.get("status");
        if (status == null || status.trim().isEmpty()) {
            return fail("Status is required");
        }

        order.setStatus(status.trim());
        orderRepo.save(order);
        return Map.of("success", true, "message", "Order status updated successfully");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Integer id, Authentication auth) {
        if (!isAdmin(auth)) {
            return fail("Access denied");
        }
        if (!orderRepo.existsById(id)) {
            return fail("Order not found");
        }

        orderService.deleteOrder(id);
        return Map.of("success", true, "message", "Order deleted successfully");
    }

    @GetMapping("/summary/{id}")
    public Map<String, Object> getOrderSummary(@PathVariable Integer id, Authentication auth) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null) {
            return fail("Order not found");
        }
        if (!canViewOrder(order, auth)) {
            return fail(auth == null ? "Not authenticated" : "Unauthorized");
        }

        List<OrderDetail> details = detailRepo.findByOrderId(id);
        double totalAmount = 0;
        int totalItems = 0;

        for (OrderDetail detail : details) {
            totalAmount += detail.getPrice() * detail.getQuantity();
            totalItems += detail.getQuantity();
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("success", true);
        summary.put("orderId", order.getId());
        summary.put("username", order.getUsername());
        summary.put("address", order.getAddress());
        summary.put("status", order.getStatus());
        summary.put("createDate", order.getCreate_date());
        summary.put("totalItems", totalItems);
        summary.put("totalAmount", totalAmount);
        return summary;
    }

    @GetMapping("/revenue")
    public Double revenue() {
        return orderService.getRevenue();
    }

    @GetMapping("/top")
    public List<Map<String, Object>> topCars() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : detailRepo.topCars()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0]);
            item.put("qty", row[1]);
            result.add(item);
        }
        return result;
    }

    private Map<String, Object> fail(String message) {
        return Map.of("success", false, "message", message);
    }
}
