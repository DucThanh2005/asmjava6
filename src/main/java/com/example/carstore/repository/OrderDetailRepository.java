package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.example.carstore.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderId(Integer orderId);

    // Tính tổng doanh thu
    @Query("SELECT SUM(d.price * d.quantity) FROM OrderDetail d")
    Double getRevenue();

    // Lấy top xe bán chạy: d.car.name lấy từ quan hệ ManyToOne
    @Query("SELECT d.car.name, SUM(d.quantity) FROM OrderDetail d " +
           "GROUP BY d.car.name " +
           "ORDER BY SUM(d.quantity) DESC")
    List<Object[]> topCars();
}