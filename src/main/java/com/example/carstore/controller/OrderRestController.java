package com.example.carstore.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.carstore.repository.*;
import com.example.carstore.entity.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderRestController {

    @Autowired 
    OrderRepository orderRepo;

    @Autowired 
    OrderDetailRepository detailRepo;

    // 📦 TẠO ĐƠN HÀNG
    @PostMapping
    public Orders create(@RequestBody List<Map<String,Object>> cart){

        Orders order = new Orders();
        order.setCreate_date(new Date());
        order.setStatus("NEW");

        orderRepo.save(order);

        for(Map<String,Object> item : cart){
            OrderDetail d = new OrderDetail();
            d.setOrder_id(order.getId());
            d.setCar_id((Integer)item.get("id"));
            d.setPrice(Double.valueOf(item.get("price").toString()));
            d.setQuantity(Integer.valueOf(item.get("qty").toString()));
            detailRepo.save(d);
        }

        return order;
    }

    // 💰 DOANH THU
    @GetMapping("/revenue")
    public Double revenue(){
        Double r = detailRepo.getRevenue();
        return r == null ? 0 : r; // tránh null
    }

    // 🔥 TOP XE BÁN CHẠY
    @GetMapping("/top")
    public List<Map<String,Object>> topCars(){

        List<Object[]> list = detailRepo.topCars();
        List<Map<String,Object>> result = new ArrayList<>();

        for(Object[] o : list){
            Map<String,Object> m = new HashMap<>();
            m.put("car_id", o[0]);
            m.put("qty", o[1]);
            result.add(m);
        }

        return result;
    }
}