package com.example.carstore.controller;

import com.example.carstore.entity.OrderDetail;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.service.CartService;
import com.example.carstore.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;

    public OrderController(CartService cartService,
                           OrderService orderService,
                           OrderRepository orderRepo,
                           OrderDetailRepository detailRepo) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.detailRepo = detailRepo;
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam(required = false) String address,
                           Model model,
                           HttpSession session) {
        if (cartService.getCart(session).isEmpty()) {
            model.addAttribute("error", "Không có sản phẩm nào để thanh toán!");
            return "cart";
        }

        model.addAttribute("cart", cartService.getCart(session).values());
        model.addAttribute("total", cartService.getTotal(session));
        model.addAttribute("address", address == null ? "" : address);
        return "checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam String address,
                             Authentication auth,
                             HttpSession session,
                             Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            Orders order = orderService.checkout(auth.getName(), address, cartService.getCart(session));
            cartService.clear(session);
            return "redirect:/order/detail/" + order.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cart", cartService.getCart(session).values());
            model.addAttribute("total", cartService.getTotal(session));
            model.addAttribute("address", address);
            return "checkout";
        }
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderRepo.findByUsername(auth.getName()));
        return "my-orders";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Integer id, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        Orders order = orderRepo.findById(id).orElse(null);
        if (order == null || !auth.getName().equals(order.getUsername())) {
            return "redirect:/order/my-orders";
        }

        List<OrderDetail> details = detailRepo.findByOrderId(id);
        double total = details.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("details", details);
        model.addAttribute("total", total);
        return "order-detail";
    }
}
