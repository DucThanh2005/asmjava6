package com.example.carstore.controller;

import com.example.carstore.entity.Car;
import com.example.carstore.service.CarService;
import com.example.carstore.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/car")
public class CarController {

    private final CarService carService;
    private final FileStorageService fileStorageService;

    public CarController(CarService carService, FileStorageService fileStorageService) {
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
    public String save(@ModelAttribute Car car,
                       @RequestParam(value = "file", required = false) MultipartFile file) {
        saveImageIfPresent(car, file);
        keepOldImageWhenEditing(car);
        carService.save(car);
        return "redirect:/car/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("car", carService.findById(id));
        return "car-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        carService.delete(id);
        return "redirect:/car/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Car car = carService.findById(id);
        if (car == null) {
            return "redirect:/car/list";
        }
        model.addAttribute("car", car);
        return "car-detail";
    }

    private void saveImageIfPresent(Car car, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        try {
            car.setImage(fileStorageService.saveImage(file));
        } catch (Exception ignored) {
            // Giữ nguyên ảnh cũ nếu upload lỗi để không làm hỏng luồng lưu xe.
        }
    }

    private void keepOldImageWhenEditing(Car car) {
        if (car.getId() == null || hasText(car.getImage())) {
            return;
        }
        Car existing = carService.findById(car.getId());
        if (existing != null && hasText(existing.getImage())) {
            car.setImage(existing.getImage());
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
