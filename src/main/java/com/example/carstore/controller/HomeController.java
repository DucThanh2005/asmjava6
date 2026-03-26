package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.carstore.service.CarService;

@Controller
public class HomeController {

    @Autowired
    CarService service;

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String q) {
        model.addAttribute("list", service.findAllFiltered(q));
        model.addAttribute("q", q != null ? q : "");
        return "index";
    }
}