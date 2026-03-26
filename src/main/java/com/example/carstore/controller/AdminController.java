package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.carstore.service.CarService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    CarService carService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCars", carService.findAll().size());
        return "dashboard";
    }
}

