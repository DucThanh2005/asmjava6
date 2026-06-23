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
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestProfileController {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    // GET profile
    @GetMapping
    public Map<String, Object> getProfile(Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        try {
            String username = auth.getName();
            Account account = accountRepo.findById(username).orElse(null);
            if (account == null) {
                return Map.of("success", false, "message", "Account not found");
            }
            return Map.of(
                    "success", true,
                    "username", account.getUsername(),
                    "fullname", account.getFullname(),
                    "email", account.getEmail(),
                    "role", account.getRole());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching profile: " + e.getMessage());
        }
    }

    // UPDATE profile
    @PutMapping
    public Map<String, Object> updateProfile(@RequestBody Account account, Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        try {
            String username = auth.getName();
            Account existing = accountRepo.findById(username).orElse(null);
            if (existing == null) {
                return Map.of("success", false, "message", "Account not found");
            }

            if (account.getFullname() != null && !account.getFullname().trim().isEmpty()) {
                existing.setFullname(account.getFullname());
            }
            if (account.getEmail() != null && !account.getEmail().trim().isEmpty()) {
                existing.setEmail(account.getEmail());
            }

            accountRepo.save(existing);
            return Map.of("success", true, "message", "Profile updated successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating profile: " + e.getMessage());
        }
    }

    // CHANGE password
    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> payload, Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        try {
            String username = auth.getName();
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");
            String confirmPassword = payload.get("confirmPassword");

            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return Map.of("success", false, "message", "Old password is required");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Map.of("success", false, "message", "New password is required");
            }
            if (!newPassword.equals(confirmPassword)) {
                return Map.of("success", false, "message", "Passwords do not match");
            }
            if (newPassword.equals(oldPassword)) {
                return Map.of("success", false, "message", "New password must be different from old password");
            }

            Account account = accountRepo.findById(username).orElse(null);
            if (account == null) {
                return Map.of("success", false, "message", "Account not found");
            }

            // Check old password
            if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
                return Map.of("success", false, "message", "Old password is incorrect");
            }

            account.setPassword(passwordEncoder.encode(newPassword));
            accountRepo.save(account);
            return Map.of("success", true, "message", "Password changed successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error changing password: " + e.getMessage());
        }
    }

    // DELETE account
    @DeleteMapping
    public Map<String, Object> deleteAccount(Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        try {
            String username = auth.getName();
            if (!accountRepo.existsById(username)) {
                return Map.of("success", false, "message", "Account not found");
            }
            accountRepo.deleteById(username);
            return Map.of("success", true, "message", "Account deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting account: " + e.getMessage());
        }
    }

    // GET profile by username (for admin)
    @GetMapping("/{username}")
    public Map<String, Object> getProfileByUsername(@PathVariable String username) {
        try {
            Account account = accountRepo.findById(username).orElse(null);
            if (account == null) {
                return Map.of("success", false, "message", "Account not found");
            }
            return Map.of(
                    "success", true,
                    "username", account.getUsername(),
                    "fullname", account.getFullname(),
                    "email", account.getEmail(),
                    "role", account.getRole());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching profile: " + e.getMessage());
        }
    }

    // VERIFY password
    @PostMapping("/verify-password")
    public Map<String, Object> verifyPassword(@RequestBody Map<String, String> payload, Authentication auth) {
        if (auth == null) {
            return Map.of("success", false, "message", "Not authenticated");
        }
        try {
            String username = auth.getName();
            String password = payload.get("password");

            if (password == null || password.trim().isEmpty()) {
                return Map.of("success", false, "message", "Password is required");
            }

            Account account = accountRepo.findById(username).orElse(null);
            if (account == null) {
                return Map.of("success", false, "message", "Account not found");
            }

            boolean matches = passwordEncoder.matches(password, account.getPassword());
            return Map.of("success", true, "matches", matches);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error verifying password: " + e.getMessage());
        }
    }
}