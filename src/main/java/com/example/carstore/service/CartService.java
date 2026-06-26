package com.example.carstore.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.carstore.entity.CartItem;

import javax.servlet.http.HttpSession;

@Service
public class CartService {

    public Map<Integer, CartItem> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Integer, CartItem> cart =
                (Map<Integer, CartItem>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        return cart;
    }

    public void add(CartItem item, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);

        if (item.getQuantity() <= 0) {
            item.setQuantity(1);
        }

        if (cart.containsKey(item.getId())) {
            CartItem old = cart.get(item.getId());
            old.setQuantity(old.getQuantity() + item.getQuantity());
        } else {
            cart.put(item.getId(), item);
        }
    }

    public void remove(Integer id, HttpSession session) {
        getCart(session).remove(id);
    }

    public void decrease(Integer id, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        CartItem item = cart.get(id);
        if (item == null) {
            return;
        }
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cart.remove(id);
        }
    }

    public void clear(HttpSession session) {
        session.removeAttribute("cart");
    }

    public double getTotal(HttpSession session) {
        return getCart(session).values()
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public int getTotalQuantity(HttpSession session) {
        return getCart(session).values()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}