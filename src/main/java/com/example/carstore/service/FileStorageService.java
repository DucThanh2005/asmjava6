package com.example.carstore.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public String saveImage(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File image is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());

        String ext = "";

        int dot = original.lastIndexOf('.');

        if (dot > 0) {
            ext = original.substring(dot);
        }

        String filename = UUID.randomUUID().toString() + ext;

        Path imagesDir = Paths.get("src/main/resources/static/images");

        Files.createDirectories(imagesDir);

        Path dest = imagesDir.resolve(filename);

        file.transferTo(dest.toFile());

        Path runtimeDir = Paths.get("target/classes/static/images");

        Files.createDirectories(runtimeDir);

        Files.copy(
                dest,
                runtimeDir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }
}