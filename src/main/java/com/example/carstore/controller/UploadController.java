package com.example.carstore.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class UploadController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception{
        Path imagesDir = Paths.get("src/main/resources/static/images");
        Files.createDirectories(imagesDir);
        Path target = imagesDir.resolve(file.getOriginalFilename());
        file.transferTo(target.toFile());
        return file.getOriginalFilename();
    }
}