package com.example.carstore.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.carstore.entity.Car;
import com.example.carstore.service.CarService;

@Controller
@RequestMapping("/car")
public class CarController {

    @Autowired
    CarService service;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("list", service.findAll());
        return "car-list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("car", new Car());
        return "car-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Car car, @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                Path imagesDir = Paths.get("src/main/resources/static/images");
                Files.createDirectories(imagesDir);
                Path target = imagesDir.resolve(file.getOriginalFilename());
                file.transferTo(target.toFile());
                car.setImage(file.getOriginalFilename());
            }
        } catch (Exception e) {
            // keep flow simple for skeleton: if upload fails, still allow saving other fields
            e.printStackTrace();
        }
        service.save(car);
        return "redirect:/car/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("car", service.findById(id));
        return "car-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/car/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Car car = service.findById(id);
        if (car == null) {
            return "redirect:/car/list";
        }
        model.addAttribute("car", car);
        return "car-detail";
    }
}