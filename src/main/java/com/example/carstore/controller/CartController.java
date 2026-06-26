package com.example.carstore.controller;

import com.example.carstore.entity.Car;
import com.example.carstore.entity.CartItem;
import com.example.carstore.service.CarService;
import com.example.carstore.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CarService carService;

    public CartController(CartService cartService, CarService carService) {
        this.cartService = cartService;
        this.carService = carService;
    }

    @GetMapping("/add/{id}")
    public String add(@PathVariable Integer id, HttpSession session) {
        addCarToCart(id, 1, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/view")
    public String view(Model model, HttpSession session) {
        addCartModel(model, session);
        return "cart";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Integer id, HttpSession session) {
        cartService.remove(id, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/increment/{id}")
    public String increment(@PathVariable Integer id, HttpSession session) {
        addCarToCart(id, 1, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/decrement/{id}")
    public String decrement(@PathVariable Integer id, HttpSession session) {
        cartService.decrease(id, session);
        return "redirect:/cart/view";
    }

    @GetMapping("/view-data")
    @ResponseBody
    public Map<String, Object> viewData(HttpSession session) {
        return cartResponse(true, null, session);
    }

    @GetMapping("/increment-api/{id}")
    @ResponseBody
    public Map<String, Object> incrementApi(@PathVariable Integer id, HttpSession session) {
        String message = addCarToCart(id, 1, session);
        return cartResponse(message == null, message, session);
    }

    @GetMapping("/decrement-api/{id}")
    @ResponseBody
    public Map<String, Object> decrementApi(@PathVariable Integer id, HttpSession session) {
        cartService.decrease(id, session);
        return cartResponse(true, null, session);
    }

    @GetMapping("/remove-api/{id}")
    @ResponseBody
    public Map<String, Object> removeApi(@PathVariable Integer id, HttpSession session) {
        cartService.remove(id, session);
        return cartResponse(true, null, session);
    }

    @PostMapping("/add-api/{id}")
    @ResponseBody
    public Map<String, Object> addApi(@PathVariable Integer id, HttpSession session) {
        String message = addCarToCart(id, 1, session);
        return cartResponse(message == null, message, session);
    }

    private String addCarToCart(Integer id, int quantity, HttpSession session) {
        Car car = carService.findById(id);
        if (car == null) {
            return "Không tìm thấy xe có id = " + id;
        }
        cartService.add(new CartItem(car.getId(), car.getName(), car.getPrice(), quantity), session);
        return null;
    }

    private void addCartModel(Model model, HttpSession session) {
        model.addAttribute("cart", cartService.getCart(session).values());
        model.addAttribute("total", cartService.getTotal(session));
    }

    private Map<String, Object> cartResponse(boolean success, String message, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        if (message != null) {
            result.put("message", message);
        }
        result.put("items", cartService.getCart(session).values());
        result.put("total", cartService.getTotal(session));
        return result;
    }
}
