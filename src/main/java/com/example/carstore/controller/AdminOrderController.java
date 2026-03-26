package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderRepository;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    OrderRepository orderRepo;

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
        orderRepo.deleteById(id);
        return "redirect:/admin/orders";
    }
}