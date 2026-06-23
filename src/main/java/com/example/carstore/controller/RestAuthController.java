package com.example.carstore.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestAuthController {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    // SIGNUP
    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Account account) {
        try {
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                return Map.of("success", false, "message", "Username is required");
            }

            if (accountRepo.existsById(account.getUsername())) {
                return Map.of("success", false, "message", "Username already exists");
            }

            if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
                return Map.of("success", false, "message", "Password is required");
            }

            if (account.getEmail() == null || account.getEmail().trim().isEmpty()) {
                return Map.of("success", false, "message", "Email is required");
            }

            // Set default values
            account.setRole("ROLE_USER");
            if (account.getFullname() == null || account.getFullname().trim().isEmpty()) {
                account.setFullname(account.getUsername());
            }

            // Encode password
            account.setPassword(passwordEncoder.encode(account.getPassword()));

            Account saved = accountRepo.save(account);
            return Map.of(
                    "success", true,
                    "message", "Account created successfully",
                    "username", saved.getUsername());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating account: " + e.getMessage());
        }
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> credentials,
            HttpServletRequest request) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Map.of("success", false, "message", "Username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            return Map.of("success", false, "message", "Password is required");
        }

        Account account = accountRepo.findById(username).orElse(null);

        if (account == null || !passwordEncoder.matches(password, account.getPassword())) {
            return Map.of("success", false, "message", "Sai tài khoản hoặc mật khẩu");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                account.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority(account.getRole())));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context);

        return Map.of(
                "success", true,
                "message", "Đăng nhập thành công",
                "username", account.getUsername(),
                "fullname", account.getFullname(),
                "email", account.getEmail(),
                "role", account.getRole());
    }

    // VALIDATE TOKEN (simple validation)
    @GetMapping("/validate")
    public Map<String, Object> validateToken(@RequestParam String username) {
        try {
            Account account = accountRepo.findById(username).orElse(null);
            if (account == null) {
                return Map.of("success", false, "message", "User not found");
            }
            return Map.of(
                    "success", true,
                    "username", account.getUsername(),
                    "fullname", account.getFullname(),
                    "role", account.getRole());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error validating token: " + e.getMessage());
        }
    }

    // CHECK USERNAME AVAILABILITY
    @GetMapping("/check-username/{username}")
    public Map<String, Object> checkUsernameAvailability(@PathVariable String username) {
        try {
            boolean exists = accountRepo.existsById(username);
            return Map.of(
                    "available", !exists,
                    "username", username);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error checking username: " + e.getMessage());
        }
    }

    // LOGOUT
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return Map.of("success", true, "message", "Logged out successfully");
    }
}
