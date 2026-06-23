package com.example.carstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.carstore.entity.SupportRequest;
import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Integer> {
    List<SupportRequest> findByTypeIgnoreCase(String type);
    long countByStatusIgnoreCase(String status);
}
