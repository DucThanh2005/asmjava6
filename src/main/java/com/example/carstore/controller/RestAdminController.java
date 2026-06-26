package com.example.carstore.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.carstore.entity.Account;
import com.example.carstore.entity.Brand;
import com.example.carstore.entity.Car;
import com.example.carstore.entity.Orders;
import com.example.carstore.repository.AccountRepository;
import com.example.carstore.repository.BrandRepository;
import com.example.carstore.repository.CarRepository;
import com.example.carstore.repository.OrderDetailRepository;
import com.example.carstore.repository.OrderRepository;
import com.example.carstore.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestAdminController {

    private final AccountRepository accountRepo;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final CarRepository carRepo;
    private final BrandRepository brandRepo;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    public RestAdminController(AccountRepository accountRepo,
                               OrderRepository orderRepo,
                               OrderDetailRepository detailRepo,
                               CarRepository carRepo,
                               BrandRepository brandRepo,
                               PasswordEncoder passwordEncoder,
                               OrderService orderService) {
        this.accountRepo = accountRepo;
        this.orderRepo = orderRepo;
        this.detailRepo = detailRepo;
        this.carRepo = carRepo;
        this.brandRepo = brandRepo;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
    }

    // ===== USERS MANAGEMENT =====

    @GetMapping("/users")
    public List<Account> getUsers() {
        return accountRepo.findAll();
    }

    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Account account) {
        try {
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                return Map.of("success", false, "message", "Username is required");
            }

            if (accountRepo.existsById(account.getUsername())) {
                return Map.of("success", false, "message", "Username already exists");
            }

            if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
                return Map.of("success", false, "message", "Password is required");
            }

            if (account.getRole() == null || account.getRole().trim().isEmpty()) {
                account.setRole("ROLE_USER");
            }

            if (account.getFullname() == null || account.getFullname().trim().isEmpty()) {
                account.setFullname(account.getUsername());
            }

            account.setPassword(passwordEncoder.encode(account.getPassword()));

            accountRepo.save(account);

            return Map.of("success", true, "message", "User created successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating user: " + e.getMessage());
        }
    }

    @PutMapping("/users/{username}")
    public Map<String, Object> updateUser(
            @PathVariable String username,
            @RequestBody Account account) {

        try {
            Account existing = accountRepo.findById(username).orElse(null);

            if (existing == null) {
                return Map.of("success", false, "message", "User not found");
            }

            if (account.getFullname() != null) {
                existing.setFullname(account.getFullname());
            }

            if (account.getEmail() != null) {
                existing.setEmail(account.getEmail());
            }

            if (account.getRole() != null) {
                existing.setRole(account.getRole());
            }

            if (account.getPassword() != null && !account.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(account.getPassword()));
            }

            accountRepo.save(existing);

            return Map.of("success", true, "message", "User updated successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{username}")
    public Map<String, Object> deleteUser(@PathVariable String username) {
        try {
            if (!accountRepo.existsById(username)) {
                return Map.of("success", false, "message", "User not found");
            }

            if (orderRepo.existsByUsername(username)) {
                return Map.of(
                        "success", false,
                        "message", "Cannot delete this user because the user already has orders. Keep the user to preserve order history.");
            }

            accountRepo.deleteById(username);

            return Map.of("success", true, "message", "User deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting user: " + e.getMessage());
        }
    }

    // ===== ORDERS MANAGEMENT =====

    @GetMapping("/orders")
    public List<Orders> getOrders() {
        return orderRepo.findAll();
    }

    @PutMapping("/orders/{id}/status")
    public Map<String, Object> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {

        try {
            Orders order = orderRepo.findById(id).orElse(null);

            if (order == null) {
                return Map.of("success", false, "message", "Order not found");
            }

            String status = payload.get("status");

            if (status == null || status.trim().isEmpty()) {
                return Map.of("success", false, "message", "Status is required");
            }

            order.setStatus(status);
            orderRepo.save(order);

            return Map.of("success", true, "message", "Order status updated successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating order: " + e.getMessage());
        }
    }

    @DeleteMapping("/orders/{id}")
    public Map<String, Object> deleteOrder(@PathVariable Integer id) {
        try {
            if (!orderRepo.existsById(id)) {
                return Map.of("success", false, "message", "Order not found");
            }

            orderService.deleteOrder(id);

            return Map.of("success", true, "message", "Order deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting order: " + e.getMessage());
        }
    }

    // ===== CARS MANAGEMENT =====

    @GetMapping("/cars")
    public List<Car> getCars() {
        return carRepo.findAll();
    }

    @PostMapping("/cars")
    public Map<String, Object> createCar(@RequestBody Car car) {
        try {
            Car saved = carRepo.save(car);

            return Map.of(
                    "success", true,
                    "message", "Car created successfully",
                    "data", saved);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating car: " + e.getMessage());
        }
    }

    @PutMapping("/cars/{id}")
    public Map<String, Object> updateCar(
            @PathVariable Integer id,
            @RequestBody Car car) {

        try {
            Car existing = carRepo.findById(id).orElse(null);

            if (existing == null) {
                return Map.of("success", false, "message", "Car not found");
            }

            if (car.getName() != null) {
                existing.setName(car.getName());
            }

            if (car.getPrice() != null) {
                existing.setPrice(car.getPrice());
            }

            if (car.getImage() != null) {
                existing.setImage(car.getImage());
            }

            if (car.getDescription() != null) {
                existing.setDescription(car.getDescription());
            }

            if (car.getBrandId() != null) {
                existing.setBrandId(car.getBrandId());
            }

            if (car.getYear() != null) {
                existing.setYear(car.getYear());
            }

            if (car.getColor() != null) {
                existing.setColor(car.getColor());
            }

            Car updated = carRepo.save(existing);

            return Map.of(
                    "success", true,
                    "message", "Car updated successfully",
                    "data", updated);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating car: " + e.getMessage());
        }
    }

    @DeleteMapping("/cars/{id}")
    public Map<String, Object> deleteCar(@PathVariable Integer id) {
        try {
            if (!carRepo.existsById(id)) {
                return Map.of("success", false, "message", "Car not found");
            }

            if (detailRepo.existsByCar_Id(id)) {
                return Map.of(
                        "success", false,
                        "message", "Cannot delete this car because it already appears in order details. Keep it to preserve order history.");
            }

            carRepo.deleteById(id);

            return Map.of("success", true, "message", "Car deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting car: " + e.getMessage());
        }
    }

    // ===== BRANDS MANAGEMENT =====

    @GetMapping("/brands")
    public List<Brand> getBrands() {
        return brandRepo.findAll();
    }

    @PostMapping("/brands")
    public Map<String, Object> createBrand(@RequestBody Brand brand) {
        try {
            Brand saved = brandRepo.save(brand);

            return Map.of(
                    "success", true,
                    "message", "Brand created successfully",
                    "data", saved);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error creating brand: " + e.getMessage());
        }
    }

    @PutMapping("/brands/{id}")
    public Map<String, Object> updateBrand(
            @PathVariable Integer id,
            @RequestBody Brand brand) {

        try {
            Brand existing = brandRepo.findById(id).orElse(null);

            if (existing == null) {
                return Map.of("success", false, "message", "Brand not found");
            }

            if (brand.getName() != null) {
                existing.setName(brand.getName());
            }

            Brand updated = brandRepo.save(existing);

            return Map.of(
                    "success", true,
                    "message", "Brand updated successfully",
                    "data", updated);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error updating brand: " + e.getMessage());
        }
    }

    @DeleteMapping("/brands/{id}")
    public Map<String, Object> deleteBrand(@PathVariable Integer id) {
        try {
            if (!brandRepo.existsById(id)) {
                return Map.of("success", false, "message", "Brand not found");
            }

            if (carRepo.countByBrandId(id) > 0) {
                return Map.of(
                        "success", false,
                        "message", "Cannot delete this brand because it is being used by cars. Delete or move those cars first.");
            }

            brandRepo.deleteById(id);

            return Map.of("success", true, "message", "Brand deleted successfully");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error deleting brand: " + e.getMessage());
        }
    }

    // ===== DASHBOARD STATS =====

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        try {
            long totalCars = carRepo.count();
            long totalUsers = accountRepo.count();
            long totalOrders = orderRepo.count();
            long totalBrands = brandRepo.count();

            Double revenue = detailRepo.getRevenue();

            if (revenue == null) {
                revenue = 0.0;
            }

            return Map.of(
                    "success", true,
                    "totalCars", totalCars,
                    "totalUsers", totalUsers,
                    "totalOrders", totalOrders,
                    "totalBrands", totalBrands,
                    "revenue", revenue);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching stats: " + e.getMessage());
        }
    }

    @GetMapping("/revenue")
    public Map<String, Object> getRevenue() {
        try {
            Double revenue = detailRepo.getRevenue();

            if (revenue == null) {
                revenue = 0.0;
            }

            return Map.of(
                    "success", true,
                    "revenue", revenue);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching revenue: " + e.getMessage());
        }
    }

    @GetMapping("/top-cars")
    public Map<String, Object> getTopCars() {
        try {
            List<Object[]> topCars = detailRepo.topCars();

            return Map.of(
                    "success", true,
                    "data", topCars);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching top cars: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard-info")
    public Map<String, Object> getDashboardInfo() {
        try {
            long adminCount = accountRepo.findAll()
                    .stream()
                    .filter(a -> "ROLE_ADMIN".equals(a.getRole()))
                    .count();

            long userCount = accountRepo.findAll()
                    .stream()
                    .filter(a -> "ROLE_USER".equals(a.getRole()))
                    .count();

            Double revenue = detailRepo.getRevenue();

            if (revenue == null) {
                revenue = 0.0;
            }

            Map<String, Object> stats = Map.of(
                    "totalCars", carRepo.count(),
                    "totalUsers", accountRepo.count(),
                    "totalOrders", orderRepo.count(),
                    "totalBrands", brandRepo.count(),
                    "revenue", revenue);

            Map<String, Object> users = Map.of(
                    "count", accountRepo.count(),
                    "admins", adminCount,
                    "users", userCount);

            return Map.of(
                    "success", true,
                    "stats", stats,
                    "users", users,
                    "topCars", detailRepo.topCars());

        } catch (Exception e) {
            return Map.of("success", false, "message", "Error fetching dashboard info: " + e.getMessage());
        }
    }
}