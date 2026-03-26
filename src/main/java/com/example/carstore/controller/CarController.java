package com.example.carstore.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
                String original = StringUtils.cleanPath(file.getOriginalFilename());
                String ext = "";
                int dot = original.lastIndexOf('.');
                if (dot > 0) {
                    ext = original.substring(dot);
                }
                String filename = java.util.UUID.randomUUID().toString() + ext;

                Path imagesDir = Paths.get("src/main/resources/static/images");
                Files.createDirectories(imagesDir);
                Path dest = imagesDir.resolve(filename);
                file.transferTo(dest.toFile());

                // Nếu chạy từ IDE fallback tới target/classes (maven run) giúp hiển thị luôn
                Path runtimeDir = Paths.get("target/classes/static/images");
                Files.createDirectories(runtimeDir);
                Files.copy(dest, runtimeDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

                car.setImage(filename);
            }

            // nếu không upload ảnh mới (edit) thì giữ ảnh cũ
            if ((car.getImage() == null || car.getImage().isBlank()) && car.getId() != null) {
                Car existing = service.findById(car.getId());
                if (existing != null && existing.getImage() != null && !existing.getImage().isBlank()) {
                    car.setImage(existing.getImage());
                }
            }

        } catch (Exception e) {
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