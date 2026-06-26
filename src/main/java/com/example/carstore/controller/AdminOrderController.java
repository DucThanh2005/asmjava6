package com.example.carstore.controller;

import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepo;
    private final OrderService orderService;

    public AdminOrderController(OrderRepository orderRepo, OrderService orderService) {
        this.orderRepo = orderRepo;
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "admin-orders";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Integer id, @RequestParam String status) {
        Orders order = orderRepo.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepo.save(order);
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        if (orderRepo.existsById(id)) {
            orderService.deleteOrder(id);
        }
        return "redirect:/admin/orders";
    }
}
