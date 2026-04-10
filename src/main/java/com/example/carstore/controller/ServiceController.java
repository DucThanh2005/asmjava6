package com.example.carstore.controller;

import com.example.carstore.mode.SupportRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ServiceController {

    List<SupportRequest> list = new ArrayList<>();

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
        list.add(req);

        model.addAttribute("message", "Gửi thành công!");
        model.addAttribute("type", type);

        return "support";
    }

    // Xem danh sách yêu cầu
    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("list", list);
        return "history";
    }

    // Giả lập xử lý xong
    @GetMapping("/done/{index}")
    public String done(@PathVariable int index) {
        list.get(index).setStatus("Đã xử lý");
        return "redirect:/history";
    }
}