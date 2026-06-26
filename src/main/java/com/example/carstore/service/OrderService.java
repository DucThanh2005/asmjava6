package com.example.carstore.service;

import com.example.carstore.entity.Car;
import com.example.carstore.entity.CartItem;
import com.example.carstore.entity.OrderDetail;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.util.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final CarService carService;

    public OrderService(OrderRepository orderRepo,
                        OrderDetailRepository detailRepo,
                        CarService carService) {
        this.orderRepo = orderRepo;
        this.detailRepo = detailRepo;
        this.carService = carService;
    }

    @Transactional
    public Orders checkout(String username, String address, Map<Integer, CartItem> cart) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("User is required");
        }
        if (!StringUtils.hasText(address)) {
            throw new IllegalArgumentException("Address is required");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Orders order = new Orders();
        order.setUsername(username);
        order.setCreate_date(new Date());
        order.setAddress(address.trim());
        order.setStatus(OrderStatus.PENDING);
        Orders savedOrder = orderRepo.save(order);

        for (CartItem item : cart.values()) {
            Car car = carService.findById(item.getId());
            if (car == null) {
                throw new IllegalArgumentException("Car not found: " + item.getId());
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity for car: " + item.getId());
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrderId(savedOrder.getId());
            detail.setCar(car);
            detail.setPrice(car.getPrice());
            detail.setQuantity(item.getQuantity());
            detailRepo.save(detail);
        }

        return savedOrder;
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        List<OrderDetail> details = detailRepo.findByOrderId(orderId);
        for (OrderDetail detail : details) {
            detailRepo.deleteById(detail.getId());
        }
        orderRepo.deleteById(orderId);
    }

    public double getRevenue() {
        Double revenue = detailRepo.getRevenue();
        return revenue == null ? 0D : revenue;
    }
}
