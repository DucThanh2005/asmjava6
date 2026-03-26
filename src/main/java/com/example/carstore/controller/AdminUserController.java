package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.carstore.entity.Account;
import com.example.carstore.repository.AccountRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    AccountRepository accountRepo;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", accountRepo.findAll());
        return "admin-users";
    }

    @GetMapping("/edit/{username}")
    public String editUser(@PathVariable String username, Model model) {
        Account account = accountRepo.findById(username).orElse(new Account());
        model.addAttribute("account", account);
        return "admin-user-form";
    }

    @PostMapping("/save")
    public String saveUser(Account account) {
        accountRepo.save(account);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username) {
        accountRepo.deleteById(username);
        return "redirect:/admin/users";
    }
}