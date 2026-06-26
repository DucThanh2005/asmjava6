package com.example.carstore.controller;

import com.example.carstore.service.SupportRequestService;
import com.example.carstore.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ServiceController {

    private final SupportRequestService supportService;

    public ServiceController(SupportRequestService supportService) {
        this.supportService = supportService;
    }

    @GetMapping("/service/book")
    public String serviceBook() {
        return "redirect:/service";
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

    @PostMapping("/service/book")
    public String handleServiceBook(@RequestParam String name,
                                    @RequestParam String phone,
                                    @RequestParam String carInfo,
                                    @RequestParam String serviceType,
                                    @RequestParam String date,
                                    @RequestParam String time,
                                    Authentication auth,
                                    Model model) {
        supportService.createServiceBooking(name, phone, carInfo, serviceType, date, time, auth);
        model.addAttribute("message", "Đặt lịch dịch vụ thành công!");
        return "service";
    }

    @PostMapping("/support")
    public String handleSupport(@RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam String content,
                                @RequestParam String type,
                                Authentication auth,
                                Model model) {
        supportService.createSupport(name, phone, type, content, auth);
        model.addAttribute("message", "Gửi thành công!");
        model.addAttribute("type", type);
        return "support";
    }

    @GetMapping("/history")
    public String history(@RequestParam(required = false) String type,
                          Authentication auth,
                          Model model) {
        model.addAttribute("list", supportService.findHistory(type, auth));
        model.addAttribute("isAdmin", SecurityUtils.isAdmin(auth));
        return "history";
    }

    @GetMapping("/done/{id}")
    public String done(@PathVariable Integer id, Authentication auth) {
        if (SecurityUtils.isAdmin(auth)) {
            supportService.markDone(id);
        }
        return "redirect:/history";
    }
}
