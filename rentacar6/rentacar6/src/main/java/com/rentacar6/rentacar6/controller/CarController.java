package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody CarDTO carDTO) {
        Car createdCar = carService.createCar(carDTO);
        return ResponseEntity.ok(createdCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        Car updatedCar = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
