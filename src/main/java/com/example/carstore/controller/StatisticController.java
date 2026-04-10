package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.carstore.repository.OrderDetailRepository;

@Controller
public class StatisticController {

    @Autowired
    OrderDetailRepository orderDetailRepo;

    @GetMapping("/admin/statistics")
    public String viewStats(Model model) {
        Double revenue = orderDetailRepo.getRevenue();
        model.addAttribute("totalRevenue", revenue != null ? revenue : 0);
        model.addAttribute("topCars", orderDetailRepo.topCars());
        return "admin/statistics"; // Khớp với tên file ở bước 4
    }
}