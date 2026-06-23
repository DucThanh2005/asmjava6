package com.example.carstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.carstore.entity.Brand;
import com.example.carstore.repository.BrandRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestBrandController {

    @Autowired
    BrandRepository brandRepo;

    // GET all brands
    @GetMapping
    public List<Brand> getAllBrands() {
        return brandRepo.findAll();
    }

    // GET brand by ID
    @GetMapping("/{id}")
    public Map<String, Object> getBrandById(@PathVariable Integer id) {
        Brand brand = brandRepo.findById(id).orElse(null);
        if (brand == null) {
            return Map.of("success", false, "message", "Brand not found");
        }
        return Map.of("success", true, "data", brand);
    }

    // CREATE brand
    @PostMapping
    public Map<String, Object> createBrand(@RequestBody Brand brand) {
        try {
            Brand saved = brandRepo.save(brand);
            return Map.of("success", true, "message", "Brand created successfully", "data", saved);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating brand: " + e.getMessage());
        }
    }

    // UPDATE brand
    @PutMapping("/{id}")
    public Map<String, Object> updateBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        Brand existing = brandRepo.findById(id).orElse(null);
        if (existing == null) {
            return Map.of("success", false, "message", "Brand not found");
        }
        existing.setName(brand.getName());
        Brand updated = brandRepo.save(existing);
        return Map.of("success", true, "message", "Brand updated successfully", "data", updated);
    }

    // DELETE brand
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBrand(@PathVariable Integer id) {
        if (!brandRepo.existsById(id)) {
            return Map.of("success", false, "message", "Brand not found");
        }
        brandRepo.deleteById(id);
        return Map.of("success", true, "message", "Brand deleted successfully");
    }
}
