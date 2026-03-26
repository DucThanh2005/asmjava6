package com.example.carstore.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.carstore.repository.CarRepository;
import com.example.carstore.entity.Car;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin("*")
public class RestCarController {

    @Autowired
    CarRepository repo;

    @GetMapping
    public List<Car> getAll(){
        return repo.findAll();
    }

    @PostMapping
    public Car create(@RequestBody Car car){
        return repo.save(car);
    }

    @PutMapping("/{id}")
    public Car update(@PathVariable Integer id, @RequestBody Car car){
        car.setId(id);
        return repo.save(car);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        repo.deleteById(id);
    }
}