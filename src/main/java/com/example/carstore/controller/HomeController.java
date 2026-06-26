package com.example.carstore.controller;

import com.example.carstore.entity.Car;
import com.example.carstore.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final CarService carService;

    public HomeController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String q) {
        List<Car> cars = carService.findAllFiltered(q);

        // Giữ đúng tên biến cũ mà giao diện index.html đang dùng: ${list}
        model.addAttribute("list", cars);
        // Giữ thêm biến ${cars} để không ảnh hưởng nếu trang khác dùng lại.
        model.addAttribute("cars", cars);
        model.addAttribute("q", q != null ? q : "");

        // Giao diện trang chủ thật sự là templates/index.html
        return "index";
    }
}
