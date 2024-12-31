package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    // Tüm araçları getir (sadece kullanılabilir olanlar)
    @GetMapping("/allCars")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = carService.getAllCars().stream()
                .filter(car -> car.getAvailableCount() > 0) // Sadece mevcut araçlar
                .map(car -> new CarDTO(
                        car.getId(),
                        car.getBrand(),
                        car.getModel(),
                        car.getYear(),
                        car.getColor(),
                        car.getGearType().toString(), // GearType enum string olarak döndürülüyor
                        car.getFuelType().toString(), // FuelType enum string olarak döndürülüyor
                        car.getKilometer(), // Kilometre bilgisi
                        car.getDailyPrice(),
                        car.isAvailable(),
                        car.getLocation().toString(), // Location enum string olarak döndürülüyor
                        car.getImageUrl(),
                        car.getAvailableCount()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }

    // Belirli bir aracı ID'ye göre getir
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        CarDTO carDTO = new CarDTO(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getGearType().toString(),
                car.getFuelType().toString(),
                car.getKilometer(),
                car.getDailyPrice(),
                car.isAvailable(),
                car.getLocation().toString(),
                car.getImageUrl(),
                car.getAvailableCount()
        );
        return ResponseEntity.ok(carDTO);
    }

    // Yeni araç oluştur
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCar(
            @RequestBody CarDTO carDTO,
            @RequestParam(required = false, defaultValue = "false") boolean confirmUpdate) {

        try {
            Car createdCar = carService.createCar(carDTO, confirmUpdate);
            return ResponseEntity.ok(createdCar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }


    // Mevcut aracı güncelle
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        Car updatedCar = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCar);
    }

    // Belirli bir aracı sil
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // Filtrelenmiş araçları getir
    @GetMapping("/filteredCars")
    public ResponseEntity<List<CarDTO>> getFilteredCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String gearType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minKilometers,
            @RequestParam(required = false) Integer maxKilometers
    ) {
        List<CarDTO> filteredCars = carService.getFilteredCars(
                brand,
                model,
                color,
                minPrice,
                maxPrice,
                year,
                gearType,
                fuelType,
                location,
                minKilometers,
                maxKilometers
        );
        return ResponseEntity.ok(filteredCars);
    }


    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations() {
        List<String> locations = Arrays.stream(LocationType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/gear-types")
    public ResponseEntity<List<String>> getGearTypes() {
        List<String> gearTypes = Arrays.stream(GearType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gearTypes);
    }

    @GetMapping("/fuel-types")
    public ResponseEntity<List<String>> getFuelTypes() {
        List<String> fuelTypes = Arrays.stream(FuelType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fuelTypes);
    }

}
