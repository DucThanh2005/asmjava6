package com.example.carstore.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.example.carstore.entity.CartItem;

import javax.servlet.http.HttpSession;

@Service
public class CartService {

    public Map<Integer, CartItem> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
		Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public void add(CartItem item, HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.containsKey(item.getId())) {
            cart.get(item.getId()).setQuantity(cart.get(item.getId()).getQuantity() + 1);
        } else {
            item.setQuantity(1);
            cart.put(item.getId(), item);
        }
    }

    public void remove(Integer id, HttpSession session) {
        getCart(session).remove(id);
    }

    public double getTotal(HttpSession session) {
        return getCart(session).values()
                .stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }
}