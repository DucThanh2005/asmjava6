package com.example.carstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.carstore.entity.Car;
import com.example.carstore.service.CarService;
import com.example.carstore.service.FileStorageService;

@Controller
@RequestMapping("/car")
public class CarController {

    private final CarService carService;
    private final FileStorageService fileStorageService;

    public CarController(
            CarService carService,
            FileStorageService fileStorageService) {

        this.carService = carService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("list", carService.findAll());
        return "car-list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("car", new Car());
        return "car-form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Car car,
            @RequestParam(value = "file", required = false)
            MultipartFile file) {

        try {

            if (file != null && !file.isEmpty()) {

                String filename =
                        fileStorageService.saveImage(file);

                car.setImage(filename);
            }

            if ((car.getImage() == null
                    || car.getImage().isBlank())
                    && car.getId() != null) {

                Car existing =
                        carService.findById(car.getId());

                if (existing != null
                        && existing.getImage() != null
                        && !existing.getImage().isBlank()) {

                    car.setImage(existing.getImage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        carService.save(car);

        return "redirect:/car/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "car",
                carService.findById(id));

        return "car-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {

        carService.delete(id);

        return "redirect:/car/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Integer id,
            Model model) {

        Car car = carService.findById(id);

        if (car == null) {
            return "redirect:/car/list";
        }

        model.addAttribute("car", car);

        return "car-detail";
    }
}