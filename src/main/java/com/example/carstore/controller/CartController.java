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
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    CarService carService;

    // =========================================================================
    // ENDPOINT THYMELEAF (GIỮ NGUYÊN)
    // =========================================================================
    @GetMapping("/add/{id}")
    public String add(@PathVariable Integer id, HttpSession session) {
        Car car = carService.findById(id);
        CartItem item = new CartItem(car.getId(), car.getName(), car.getPrice(), 1);
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

    // =========================================================================
    // API CHUẨN ĐỒNG BỘ ĐA NĂNG CHO VUEJS (TRẢ VỀ JSON CHÍNH XÁC)
    // =========================================================================

    // 1. API lấy thông tin giỏ hàng hiện tại
    @GetMapping("/view-data")
    @ResponseBody
    public Map<String, Object> viewData(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("items", cartService.getCart(session).values());
        result.put("total", cartService.getTotal(session));
        return result;
    }

    // 2. API tăng số lượng xe (Xử lý nút +)
    @GetMapping("/increment-api/{id}")
    @ResponseBody
    public Map<String, Object> incrementApi(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Car car = carService.findById(id);
            CartItem item = new CartItem(car.getId(), car.getName(), car.getPrice(), 1);
            cartService.add(item, session); // Hàm add mặc định tự tăng số lượng nếu đã có xe

            result.put("success", true);
            result.put("items", cartService.getCart(session).values());
            result.put("total", cartService.getTotal(session));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 3. API giảm số lượng xe (Xử lý nút - không bị xóa xe)
    @GetMapping("/decrement-api/{id}")
    @ResponseBody
    public Map<String, Object> decrementApi(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<Integer, CartItem> cart = cartService.getCart(session);
            if (cart != null && cart.containsKey(id)) {
                CartItem item = cart.get(id);
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    cartService.remove(id, session); // Bằng 1 sản phẩm thì xóa hẳn khỏi giỏ
                }
            }
            result.put("success", true);
            result.put("items", cartService.getCart(session).values());
            result.put("total", cartService.getTotal(session));
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }

    // 4. API xóa sản phẩm trực tiếp từ giỏ hàng cho Vue
    @GetMapping("/remove-api/{id}")
    @ResponseBody
    public Map<String, Object> removeApi(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            cartService.remove(id, session);
            result.put("success", true);
            result.put("items", cartService.getCart(session).values());
            result.put("total", cartService.getTotal(session));
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }

    @PostMapping("/add-api/{id}")
    @ResponseBody
    public Map<String, Object> addApi(
            @PathVariable Integer id,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        try {

            System.out.println("========== ADD TO CART ==========");
            System.out.println("ID = " + id);

            Car car = carService.findById(id);

            System.out.println("CAR = " + car);

            if (car == null) {
                throw new RuntimeException("Không tìm thấy xe có id = " + id);
            }

            CartItem item = new CartItem(
                    car.getId(),
                    car.getName(),
                    car.getPrice(),
                    1);

            cartService.add(item, session);

            System.out.println("ADD SUCCESS");

            result.put("success", true);
            result.put("items", cartService.getCart(session).values());
            result.put("total", cartService.getTotal(session));

        } catch (Exception e) {

            e.printStackTrace();

            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}