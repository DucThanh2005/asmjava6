package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.SupportRequest;
import com.example.carstore.repository.SupportRequestRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestServiceController {

    @Autowired
    private SupportRequestRepository supportRepo;

    @PostConstruct
    public void initSupportRequests() {
        if (supportRepo.count() == 0) {
            supportRepo.save(new SupportRequest("Nguyễn Văn A", "0901234567", "chat",
                    "Tôi muốn được tư vấn thêm về xe Toyota Camry"));
            supportRepo.save(new SupportRequest("Trần Thị B", "0987654321", "baohanh",
                    "Xe Honda Civic cần bảo hành hệ thống phanh"));
            supportRepo.save(new SupportRequest("Lê Văn C", "0912345678", "doitra",
                    "Yêu cầu đổi xe do lỗi kỹ thuật sau khi nhận"));
            supportRepo.save(new SupportRequest("Phạm Thị D", "0933222111", "dichvu",
                    "Đặt lịch bảo dưỡng định kỳ cho xe Mercedes C300"));
        }
    }

    // GET all support requests
    @GetMapping
    public Map<String, Object> getAllSupportRequests() {
        return Map.of("success", true, "data", supportRepo.findAll());
    }

    // GET support request by id
    @GetMapping("/{id}")
    public Map<String, Object> getSupportRequest(@PathVariable Integer id) {
        return supportRepo.findById(id)
                .map(request -> Map.of("success", true, "data", request))
                .orElse(Map.of("success", false, "message", "Support request not found"));
    }

    // CREATE support request
    @PostMapping
    public Map<String, Object> createSupportRequest(@RequestBody SupportRequest request) {
        try {
            SupportRequest saved = supportRepo.save(request);
            return Map.of(
                    "success", true,
                    "message", "Support request created successfully",
                    "id", saved.getId());
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating support request: " + e.getMessage());
        }
    }

    // UPDATE support request status
    @PutMapping("/{id}/status")
    public Map<String, Object> updateSupportStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        return supportRepo.findById(id).map(request -> {
            String status = payload.get("status");
            request.setStatus(status);
            supportRepo.save(request);
            return Map.<String, Object>of("success", true, "message", "Support request status updated successfully");
        }).orElse(Map.of("success", false, "message", "Support request not found"));
    }

    // DELETE support request
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSupportRequest(@PathVariable Integer id) {
        if (!supportRepo.existsById(id)) {
            return Map.of("success", false, "message", "Support request not found");
        }
        supportRepo.deleteById(id);
        return Map.of("success", true, "message", "Support request deleted successfully");
    }

    // GET support requests by type
    @GetMapping("/type/{type}")
    public Map<String, Object> getSupportRequestsByType(@PathVariable String type) {
        try {
            return Map.of("success", true, "data", supportRepo.findByTypeIgnoreCase(type));
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error filtering support requests: " + e.getMessage());
        }
    }

    // Get stats
    @GetMapping("/stats")
    public Map<String, Object> getSupportStats() {
        try {
            long total = supportRepo.count();
            long pending = supportRepo.countByStatusIgnoreCase("Chờ xử lý")
                    + supportRepo.countByStatusIgnoreCase("Đang xử lý");
            long resolved = supportRepo.countByStatusIgnoreCase("Đã xử lý");

            return Map.of(
                    "success", true,
                    "total", total,
                    "pending", pending,
                    "resolved", resolved);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error calculating stats: " + e.getMessage());
        }
    }
}
