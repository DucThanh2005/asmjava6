package com.example.carstore.controller;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;
import com.example.carstore.repository.SupportRequestRepository;
import com.example.carstore.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final SupportRequestRepository supportRepo;

    public ProfileController(AccountRepository accountRepo,
                             PasswordEncoder passwordEncoder,
                             SupportRequestRepository supportRepo) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.supportRepo = supportRepo;
    }

    @GetMapping
    public String profile(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        loadProfileModel(auth, model, null);
        return "profile";
    }

    @PostMapping("/update")
    public String update(Account account, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        Account existing = accountRepo.findById(auth.getName()).orElse(null);
        if (existing != null) {
            existing.setFullname(account.getFullname());
            existing.setEmail(account.getEmail());
            if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(account.getPassword()));
            }
            accountRepo.save(existing);
            model.addAttribute("success", "Cập nhật thành công!");
        }

        loadProfileModel(auth, model, existing);
        return "profile";
    }

    private void loadProfileModel(Authentication auth, Model model, Account account) {
        Account current = account != null ? account : accountRepo.findById(auth.getName()).orElse(null);
        model.addAttribute("account", current);
        model.addAttribute("list", SecurityUtils.isAdmin(auth)
                ? supportRepo.findAll()
                : supportRepo.findByUsernameIgnoreCase(auth.getName()));
    }
}
