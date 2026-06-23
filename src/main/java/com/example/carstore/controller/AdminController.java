package com.example.carstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.carstore.repository.AccountRepository;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.service.CarService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CarService carService;
    private final AccountRepository accountRepo;
    private final OrderRepository orderRepo;

    public AdminController(
            CarService carService,
            AccountRepository accountRepo,
            OrderRepository orderRepo) {

        this.carService = carService;
        this.accountRepo = accountRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalCars",
                carService.findAll().size());

        model.addAttribute(
                "totalUsers",
                accountRepo.findAll().size());

        model.addAttribute(
                "totalOrders",
                orderRepo.findAll().size());

        return "dashboard";
    }
}