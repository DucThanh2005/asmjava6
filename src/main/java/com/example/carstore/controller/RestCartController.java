package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.Car;
import com.example.carstore.entity.CartItem;
import com.example.carstore.service.CarService;
import com.example.carstore.service.CartService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class RestCartController {

    @Autowired
    CartService cartService;

    @Autowired
    CarService carService;

    @PostMapping("/add/{id}")
    public Map<String, Object> addToCart(@PathVariable Integer id, HttpSession session) {
        Car car = carService.findById(id);
        if (car == null) {
            return Map.of("success", false, "message", "Car not found");
        }
        CartItem item = new CartItem(car.getId(), car.getName(), car.getPrice(), 1);
        cartService.add(item, session);
        return Map.of("success", true, "message", "Added to cart");
    }

    @GetMapping
    public Map<String, Object> getCart(HttpSession session) {
        return Map.of(
            "items", cartService.getCart(session).values(),
            "total", cartService.getTotal(session)
        );
    }

    @DeleteMapping("/remove/{id}")
    public Map<String, Object> removeFromCart(@PathVariable Integer id, HttpSession session) {
        cartService.remove(id, session);
        return Map.of("success", true, "message", "Removed from cart");
    }
}