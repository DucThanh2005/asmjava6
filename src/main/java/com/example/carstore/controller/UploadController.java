package com.example.carstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File không được để trống");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên file không hợp lệ");
        }

        String cleanName = Paths.get(originalName).getFileName().toString();
        String extension = "";
        int dotIndex = cleanName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < cleanName.length() - 1) {
            extension = cleanName.substring(dotIndex + 1).toLowerCase();
        }

        String contentType = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(extension) || contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Chỉ cho phép upload file ảnh: jpg, jpeg, png, gif, webp");
        }

        String savedFileName = UUID.randomUUID() + "." + extension;

        Path sourceImagesDir = Paths.get("src/main/resources/static/images");
        Files.createDirectories(sourceImagesDir);
        Path sourceTarget = sourceImagesDir.resolve(savedFileName).normalize();
        file.transferTo(sourceTarget.toFile());

        // Khi chạy bằng IDE, Spring Boot thường phục vụ file từ target/classes.
        // Copy thêm vào target để ảnh vừa upload có thể hiển thị ngay khi đang chạy app.
        Path runtimeImagesDir = Paths.get("target/classes/static/images");
        if (Files.exists(Paths.get("target/classes"))) {
            Files.createDirectories(runtimeImagesDir);
            Files.copy(sourceTarget, runtimeImagesDir.resolve(savedFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.ok(savedFileName);
    }
}
