package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CarService carService;

    // Mevcut araçların listesi
    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    // Araç ekleme
    @PostMapping("/cars")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        return ResponseEntity.ok(carService.createCar(car));
    }

    // Araç bilgilerini güncelleme
    @PutMapping("/cars/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car carDetails) {
        return ResponseEntity.ok(carService.updateCar(id, carDetails));
    }

    // Araç silme
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // Araç durumunu değiştirme (ör. "kiralanabilir" veya "kiralanamaz")
    @PutMapping("/cars/{id}/availability")
    public ResponseEntity<Car> updateCarAvailability(@PathVariable Long id, @RequestParam boolean available) {
        Car car = carService.getCarById(id);
        car.setAvailable(available);
        return ResponseEntity.ok(carService.updateCar(id, car));
    }
}
