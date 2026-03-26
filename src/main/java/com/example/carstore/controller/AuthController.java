package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

@Controller
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/form")
    public String loginFormRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("account", new Account());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(Account account, Model model) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            return "signup";
        }

        if (accountRepository.existsById(account.getUsername())) {
            model.addAttribute("error", "Tài khoản đã tồn tại.");
            return "signup";
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole("ROLE_USER");
        accountRepository.save(account);

        return "redirect:/login?registered";
    }
}