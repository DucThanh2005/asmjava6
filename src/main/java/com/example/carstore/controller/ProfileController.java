package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping
    public String profile(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        String username = auth.getName();
        Account account = accountRepo.findById(username).orElse(null);
        model.addAttribute("account", account);
        return "profile";
    }

    @PostMapping("/update")
    public String update(Account account, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        String username = auth.getName();
        Account existing = accountRepo.findById(username).orElse(null);
        if (existing != null) {
            existing.setFullname(account.getFullname());
            existing.setEmail(account.getEmail());
            if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(account.getPassword()));
            }
            accountRepo.save(existing);
            model.addAttribute("success", "Cập nhật thành công!");
        }
        model.addAttribute("account", existing);
        return "profile";
    }
}