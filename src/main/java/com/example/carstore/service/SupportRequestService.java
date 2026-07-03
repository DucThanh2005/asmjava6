package com.example.carstore.service;

import com.example.carstore.entity.SupportRequest;
import com.example.carstore.repository.SupportRequestRepository;
import com.example.carstore.util.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SupportRequestService {

    public static final String STATUS_PENDING = "Chờ xử lý";
    public static final String STATUS_DONE = "Đã xử lý";

    private final SupportRequestRepository supportRepo;

    public SupportRequestService(SupportRequestRepository supportRepo) {
        this.supportRepo = supportRepo;
    }

    public SupportRequest createSupport(
            String name,
            String phone,
            String type,
            String content,
            Authentication auth) {
        SupportRequest request = new SupportRequest(
                name,
                phone,
                type,
                content);
        request.setUsername(SecurityUtils.username(auth));
        request.setStatus(STATUS_PENDING);
        return supportRepo.save(request);
        
    }

    public SupportRequest createServiceBooking(
            String name,
            String phone,
            String carInfo,
            String serviceType,
            String date,
            String time,
            Authentication auth) {
        SupportRequest request = new SupportRequest(
                name,
                phone,
                "service",
                "Yêu cầu đặt lịch dịch vụ",
                carInfo,
                serviceType,
                date,
                time);
        request.setUsername(SecurityUtils.username(auth));
        request.setStatus(STATUS_PENDING);
        return supportRepo.save(request);
    }

    public List<SupportRequest> findHistory(String type, Authentication auth) {
        if (SecurityUtils.isAdmin(auth)) {
            return StringUtils.hasText(type)
                    ? supportRepo.findByTypeIgnoreCase(type)
                    : supportRepo.findAll();
        }

        String username = SecurityUtils.username(auth);
        return StringUtils.hasText(type)
                ? supportRepo.findByUsernameIgnoreCaseAndTypeIgnoreCase(username, type)
                : supportRepo.findByUsernameIgnoreCase(username);
    }

    public boolean markDone(Integer id) {
        return supportRepo.findById(id).map(request -> {
            request.setStatus(STATUS_DONE);
            supportRepo.save(request);
            return true;
        }).orElse(false);
    }
}
