package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin("*")
public class RestProfileController {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping
    public Map<String, Object> getProfile(Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        String username = auth.getName();
        Account account = accountRepo.findById(username).orElse(null);
        if (account == null) {
            return Map.of("success", false, "message", "Account not found");
        }
        return Map.of("success", true, "account", account);
    }

    @PutMapping
    public Map<String, Object> updateProfile(@RequestBody Account account, Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        String username = auth.getName();
        Account existing = accountRepo.findById(username).orElse(null);
        if (existing == null) {
            return Map.of("success", false, "message", "Account not found");
        }
        existing.setFullname(account.getFullname());
        existing.setEmail(account.getEmail());
        if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        accountRepo.save(existing);
        return Map.of("success", true, "message", "Profile updated successfully");
    }
}