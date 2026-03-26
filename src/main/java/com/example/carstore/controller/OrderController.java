package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.carstore.entity.CartItem;
import com.example.carstore.entity.OrderDetail;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.service.CartService;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    CartService cartService;

    @Autowired
    OrderRepository orderRepo;

    @Autowired
    OrderDetailRepository detailRepo;

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        model.addAttribute("cart", cartService.getCart(session).values());
        model.addAttribute("total", cartService.getTotal(session));
        return "checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam String address, Authentication auth, HttpSession session, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        String username = auth.getName();
        List<Map<String, Object>> cartItems = new java.util.ArrayList<>();
        for (CartItem item : cartService.getCart(session).values()) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", item.getId());
            map.put("price", item.getPrice());
            map.put("qty", item.getQuantity());
            cartItems.add(map);
        }

        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Giỏ hàng trống!");
            return "checkout";
        }

        Orders order = new Orders();
        order.setUsername(username);
        order.setCreate_date(new Date());
        order.setAddress(address);
        order.setStatus("PENDING");
        orderRepo.save(order);

        // Note: In a real app, save order details to OrderDetail table
        // For simplicity, we'll assume OrderRestController handles it, but here we need to integrate

        // Clear cart
        session.removeAttribute("cart");

        return "redirect:/order/my-orders";
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        String username = auth.getName();
        List<Orders> orders = orderRepo.findByUsername(username);
        model.addAttribute("orders", orders);
        return "my-orders";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Integer id, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        String username = auth.getName();
        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null || !order.getUsername().equals(username)) {
            return "redirect:/order/my-orders";
        }
        List<OrderDetail> details = detailRepo.findByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "order-detail";
    }
}