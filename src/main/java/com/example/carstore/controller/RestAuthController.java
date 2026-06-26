package com.example.carstore.controller;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;
import com.example.carstore.util.ResponseUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestAuthController {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    public RestAuthController(AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Account account) {
        String validation = validateSignup(account);
        if (validation != null) {
            return ResponseUtils.fail(validation);
        }

        account.setRole("ROLE_USER");
        if (isBlank(account.getFullname())) {
            account.setFullname(account.getUsername());
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        Account saved = accountRepo.save(account);
        return Map.of(
                "success", true,
                "message", "Account created successfully",
                "username", saved.getUsername());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials,
                                     HttpServletRequest request) {
        String username = credentials == null ? null : credentials.get("username");
        String password = credentials == null ? null : credentials.get("password");

        if (isBlank(username)) {
            return ResponseUtils.fail("Username is required");
        }
        if (isBlank(password)) {
            return ResponseUtils.fail("Password is required");
        }

        Account account = accountRepo.findById(username).orElse(null);
        if (account == null || !passwordEncoder.matches(password, account.getPassword())) {
            return ResponseUtils.fail("Sai tài khoản hoặc mật khẩu");
        }

        saveLoginSession(account, request);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Đăng nhập thành công");
        result.put("username", account.getUsername());
        result.put("fullname", account.getFullname());
        result.put("email", account.getEmail());
        result.put("role", account.getRole());
        return result;
    }

    @GetMapping("/validate")
    public Map<String, Object> validateToken(@RequestParam String username) {
        Account account = accountRepo.findById(username).orElse(null);
        if (account == null) {
            return ResponseUtils.fail("User not found");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("username", account.getUsername());
        result.put("fullname", account.getFullname());
        result.put("role", account.getRole());
        return result;
    }

    @GetMapping("/check-username/{username}")
    public Map<String, Object> checkUsernameAvailability(@PathVariable String username) {
        return Map.of("available", !accountRepo.existsById(username), "username", username);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        return ResponseUtils.ok("Logged out successfully");
    }

    private String validateSignup(Account account) {
        if (account == null || isBlank(account.getUsername())) return "Username is required";
        if (accountRepo.existsById(account.getUsername())) return "Username already exists";
        if (isBlank(account.getPassword())) return "Password is required";
        if (isBlank(account.getEmail())) return "Email is required";
        return null;
    }

    private void saveLoginSession(Account account, HttpServletRequest request) {
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
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
