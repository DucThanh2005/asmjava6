package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.Car;
import com.example.carstore.entity.CartItem;
import com.example.carstore.service.CarService;
import com.example.carstore.service.CartService;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class RestCartController {

    @Autowired
    CartService cartService;

    @Autowired
    CarService carService;

    // GET cart
    @GetMapping
    public Map<String, Object> getCart(HttpSession session) {
        try {
            Collection<CartItem> items = cartService.getCart(session).values();
            Double total = cartService.getTotal(session);
            return Map.of(
                "success", true,
                "items", items,
                "total", total,
                "count", items.size()
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching cart: " + e.getMessage());
        }
    }

    // ADD to cart
    @PostMapping("/add/{id}")
    public Map<String, Object> addToCart(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer quantity, HttpSession session) {
        try {
            Car car = carService.findById(id);
            if (car == null) {
                return Map.of("success", false, "message", "Car not found");
            }
            CartItem item = new CartItem(car.getId(), car.getName(), car.getPrice(), quantity != null ? quantity : 1);
            cartService.add(item, session);
            return Map.of(
                "success", true,
                "message", "Added to cart",
                "carName", car.getName(),
                "quantity", quantity
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error adding to cart: " + e.getMessage());
        }
    }

    // REMOVE from cart
    @DeleteMapping("/remove/{id}")
    public Map<String, Object> removeFromCart(@PathVariable Integer id, HttpSession session) {
        try {
            cartService.remove(id, session);
            return Map.of("success", true, "message", "Removed from cart");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error removing from cart: " + e.getMessage());
        }
    }

    // UPDATE cart item quantity
    @PutMapping("/update/{id}")
    public Map<String, Object> updateCartItem(@PathVariable Integer id, @RequestBody Map<String, Integer> payload, HttpSession session) {
        try {
            Integer quantity = payload.get("quantity");
            if (quantity == null || quantity < 1) {
                return Map.of("success", false, "message", "Invalid quantity");
            }
            
            Map<Integer, CartItem> cart = cartService.getCart(session);
            CartItem item = cart.get(id);
            if (item == null) {
                return Map.of("success", false, "message", "Item not in cart");
            }
            
            item.setQuantity(quantity);
            return Map.of(
                "success", true,
                "message", "Cart item updated",
                "quantity", quantity,
                "total", cartService.getTotal(session)
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating cart: " + e.getMessage());
        }
    }

    // CLEAR cart
    @DeleteMapping("/clear")
    public Map<String, Object> clearCart(HttpSession session) {
        try {
            session.removeAttribute("cart");
            return Map.of("success", true, "message", "Cart cleared");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error clearing cart: " + e.getMessage());
        }
    }

    // GET cart stats
    @GetMapping("/stats")
    public Map<String, Object> getCartStats(HttpSession session) {
        try {
            Collection<CartItem> items = cartService.getCart(session).values();
            int itemCount = items.size();
            int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
            Double totalPrice = cartService.getTotal(session);
            
            return Map.of(
                "success", true,
                "itemCount", itemCount,
                "totalQuantity", totalQuantity,
                "totalPrice", totalPrice
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error getting cart stats: " + e.getMessage());
        }
    }

    // CHECK if item in cart
    @GetMapping("/has/{id}")
    public Map<String, Object> hasItem(@PathVariable Integer id, HttpSession session) {
        try {
            Map<Integer, CartItem> cart = cartService.getCart(session);
            boolean exists = cart.containsKey(id);
            CartItem item = cart.get(id);
            
            return Map.of(
                "success", true,
                "has", exists,
                "item", item
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error checking cart: " + e.getMessage());
        }
    }

    // INCREMENT item quantity
    @PostMapping("/increment/{id}")
    public Map<String, Object> incrementQuantity(@PathVariable Integer id, HttpSession session) {
        try {
            Map<Integer, CartItem> cart = cartService.getCart(session);
            CartItem item = cart.get(id);
            if (item == null) {
                return Map.of("success", false, "message", "Item not in cart");
            }
            item.setQuantity(item.getQuantity() + 1);
            return Map.of(
                "success", true,
                "quantity", item.getQuantity(),
                "total", cartService.getTotal(session)
            );
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error incrementing quantity: " + e.getMessage());
        }
    }

    // DECREMENT item quantity
    @PostMapping("/decrement/{id}")
    public Map<String, Object> decrementQuantity(@PathVariable Integer id, HttpSession session) {
        try {
            Map<Integer, CartItem> cart = cartService.getCart(session);
            CartItem item = cart.get(id);
            if (item == null) {
                return Map.of("success", false, "message", "Item not in cart");
            }
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                return Map.of(
                    "success", true,
                    "quantity", item.getQuantity(),
                    "total", cartService.getTotal(session)
                );
            } else {
                cartService.remove(id, session);
                return Map.of(
                    "success", true,
                    "message", "Item removed from cart",
                    "total", cartService.getTotal(session)
                );
            }
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error decrementing quantity: " + e.getMessage());
        }
    }
}