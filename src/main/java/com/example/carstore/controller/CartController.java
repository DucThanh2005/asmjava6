package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.carstore.entity.Car;
import com.example.carstore.entity.CartItem;
import com.example.carstore.service.CarService;
import com.example.carstore.service.CartService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    CarService carService;

    @GetMapping("/add/{id}")
    public String add(@PathVariable Integer id, HttpSession session) {
        Car car = carService.findById(id);

        CartItem item = new CartItem(
                car.getId(),
                car.getName(),
                car.getPrice(),
                1
        );

        cartService.add(item, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String view(Model model, HttpSession session) {
        model.addAttribute("cart", cartService.getCart(session).values());
        model.addAttribute("total", cartService.getTotal(session));
        return "cart";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Integer id, HttpSession session) {
        cartService.remove(id, session);
        return "redirect:/cart/view";
    }
}