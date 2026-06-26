package com.example.carstore.controller;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        String validation = validateSignup(account);
        if (validation != null) {
            model.addAttribute("error", validation);
            return "signup";
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole("ROLE_USER");
        accountRepository.save(account);
        return "redirect:/login?registered";
    }

    private String validateSignup(Account account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()
                || account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            return "Vui lòng điền đầy đủ thông tin.";
        }
        if (accountRepository.existsById(account.getUsername())) {
            return "Tài khoản đã tồn tại.";
        }
        return null;
    }
}
