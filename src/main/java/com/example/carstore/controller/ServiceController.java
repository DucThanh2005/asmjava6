package com.example.carstore.controller;

import com.example.carstore.entity.SupportRequest;
import com.example.carstore.repository.SupportRequestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ServiceController {

    private final SupportRequestRepository supportRepo;

    ServiceController(SupportRequestRepository supportRepo) {
        this.supportRepo = supportRepo;
    }

    @GetMapping("/service/book")
    public String serviceBook() {
        return "redirect:http://localhost:5173/car/services";
    }

    @GetMapping("/service")
    public String service() {
        return "service";
    }

    @GetMapping("/support")
    public String support(@RequestParam(defaultValue = "chat") String type, Model model) {
        model.addAttribute("type", type);
        return "support";
    }

    @PostMapping("/support")
    public String handleSupport(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String content,
            @RequestParam String type,
            Model model) {

        SupportRequest req = new SupportRequest(name, phone, type, content);
        supportRepo.save(req);

        model.addAttribute("message", "Gửi thành công!");
        model.addAttribute("type", type);

        return "support";
    }

    // Xem danh sách yêu cầu
    @GetMapping("/history")
    public String history(
            @RequestParam(required = false) String type,
            Model model) {

        if (type != null) {
            model.addAttribute("list",
                    supportRepo.findByTypeIgnoreCase(type));
        } else {
            model.addAttribute("list",
                    supportRepo.findAll());
        }

        return "history";
    }

    // Giả lập xử lý xong
    @GetMapping("/done/{id}")
    public String done(@PathVariable Integer id) {
        supportRepo.findById(id).ifPresent(req -> {
            req.setStatus("Đã xử lý");
            supportRepo.save(req);
        });
        return "redirect:/history";
    }
}