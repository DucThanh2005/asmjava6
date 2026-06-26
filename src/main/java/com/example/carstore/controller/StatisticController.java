package com.example.carstore.controller;

import com.example.carstore.repository.OrderDetailRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

    private final OrderDetailRepository orderDetailRepository;

    public StatisticController(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping("/admin/statistics")
    public String statistics(Model model) {
        Double totalRevenue = orderDetailRepository.getRevenue();
        model.addAttribute("totalRevenue", totalRevenue == null ? 0 : totalRevenue);
        model.addAttribute("topCars", orderDetailRepository.topCars());
        return "statistics";
    }
}
