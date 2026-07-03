package com.example.carstore.controller;

import com.example.carstore.entity.SupportRequest;
import com.example.carstore.repository.SupportRequestRepository;
import com.example.carstore.service.SupportRequestService;
import com.example.carstore.util.ResponseUtils;
import com.example.carstore.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestServiceController {

    private final SupportRequestRepository supportRepo;

    public RestServiceController(SupportRequestRepository supportRepo) {
        this.supportRepo = supportRepo;
    }

    @GetMapping
    public Map<String, Object> getAllSupportRequests() {
        return Map.of("success", true, "data", supportRepo.findAll());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getSupportRequest(@PathVariable Integer id) {
        return supportRepo.findById(id)
                .map(request -> Map.<String, Object>of("success", true, "data", request))
                .orElse(ResponseUtils.fail("Support request not found"));
    }

    @PostMapping
    public Map<String, Object> createSupportRequest(@RequestBody SupportRequest request, Authentication auth) {
        if (request == null) {
            return ResponseUtils.fail("Support request is required");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            request.setUsername(SecurityUtils.username(auth));
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus(SupportRequestService.STATUS_PENDING);
        }

        SupportRequest saved = supportRepo.save(request);
        return Map.of(
                "success", true,
                "message", "Support request created successfully",
                "id", saved.getId());
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateSupportStatus(@PathVariable Integer id,
            @RequestBody Map<String, String> payload,
            Authentication auth) {
        if (!SecurityUtils.isAdmin(auth)) {
            return ResponseUtils.fail("Access denied");
        }

        String status = payload == null ? null : payload.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseUtils.fail("Status is required");
        }

        return supportRepo.findById(id).map(request -> {
            request.setStatus(status.trim());
            supportRepo.save(request);
            return ResponseUtils.ok("Support request status updated successfully");
        }).orElse(ResponseUtils.fail("Support request not found"));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSupportRequest(@PathVariable Integer id, Authentication auth) {
        if (!SecurityUtils.isAdmin(auth)) {
            return ResponseUtils.fail("Access denied");
        }
        if (!supportRepo.existsById(id)) {
            return ResponseUtils.fail("Support request not found");
        }
        supportRepo.deleteById(id);
        return ResponseUtils.ok("Support request deleted successfully");
    }

    @GetMapping("/type/{type}")
    public Map<String, Object> getSupportRequestsByType(@PathVariable String type, Authentication auth) {
        if (!SecurityUtils.isAdmin(auth)) {
            return ResponseUtils.fail("Access denied");
        }
        return Map.of("success", true, "data", supportRepo.findByTypeIgnoreCase(type));
    }

    @GetMapping("/stats")
    public Map<String, Object> getSupportStats(Authentication auth) {
        if (!SecurityUtils.isAdmin(auth)) {
            return ResponseUtils.fail("Access denied");
        }

        long total = supportRepo.count();
        long pending = supportRepo.countByStatusIgnoreCase(SupportRequestService.STATUS_PENDING)
                + supportRepo.countByStatusIgnoreCase("Đang xử lý");
        long resolved = supportRepo.countByStatusIgnoreCase(SupportRequestService.STATUS_DONE);

        return Map.of(
                "success", true,
                "total", total,
                "pending", pending,
                "resolved", resolved);
    }
}
