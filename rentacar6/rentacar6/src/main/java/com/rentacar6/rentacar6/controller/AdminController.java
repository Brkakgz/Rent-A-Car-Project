package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.service.CarService;
import com.rentacar6.rentacar6.service.CustomerService;
import com.rentacar6.rentacar6.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Bu kontrol tüm endpointlere admin erişimini zorunlu kılar
public class AdminController {

    private final CarService carService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public AdminController(CarService carService, CustomerService customerService, OrderService orderService) {
        this.carService = carService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    // Tüm kullanıcıları görüntüleme
    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers() {
        List<String> users = customerService.getAllCustomers()
                .stream()
                .map(customer -> "T.C. No: " + customer.getTcNo() + " - " + customer.getFirstName() + " " + customer.getLastName())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Mevcut araçların listesi
    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = carService.getAllCars().stream()
                .map(carService::toCarDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }

    // Araç ekleme
    @PostMapping("/cars")
    public ResponseEntity<?> addCar(
            @RequestBody CarDTO carDTO,
            @RequestParam(required = false, defaultValue = "false") boolean confirmUpdate
    ) {
        System.out.println("Received CarDTO: " + carDTO);
        try {
            Car car = carService.createCar(carDTO, confirmUpdate);
            return ResponseEntity.ok(car);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cars/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicateCar(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam String color,
            @RequestParam String gearType,
            @RequestParam String fuelType,
            @RequestParam String location,
            @RequestParam double dailyPrice
    ) {
        Optional<Car> existingCar = carService.findCarByMatchingDetails(
                brand, model, year, color,
                GearType.valueOf(gearType.toUpperCase()),
                FuelType.valueOf(fuelType.toUpperCase()),
                LocationType.valueOf(location.toUpperCase()),
                dailyPrice
        );

        return ResponseEntity.ok(existingCar.isPresent());
    }




    // Araç bilgilerini güncelleme
    // Araç bilgilerini güncelleme veya mevcut kayıt varsa onay alarak sayısını artırma
    @PutMapping("/cars/{id}")
    public ResponseEntity<Map<String, String>> updateCar(
            @PathVariable Long id,
            @RequestBody CarDTO carDTO,
            @RequestParam(required = false, defaultValue = "false") boolean confirmUpdate) {
        System.out.println("Received PUT request for car ID: " + id);
        System.out.println("CarDTO: " + carDTO);
        Map<String, String> response = new HashMap<>();
        try {
            // Mevcut araç bilgilerini al
            Car existingCar = carService.getCarById(id);

            // Aynı özelliklere sahip başka bir araç var mı kontrol et
            Optional<Car> duplicateCar = carService.findCarByMatchingDetails(
                    carDTO.getBrand(),
                    carDTO.getModel(),
                    carDTO.getYear(),
                    carDTO.getColor(),
                    GearType.valueOf(carDTO.getGearType().toUpperCase()),
                    FuelType.valueOf(carDTO.getFuelType().toUpperCase()),
                    LocationType.valueOf(carDTO.getLocation().toUpperCase()),
                    carDTO.getDailyPrice()
            );

            if (duplicateCar.isPresent() && !duplicateCar.get().getId().equals(id)) {
                // Eğer aynı özelliklere sahip başka bir araç varsa
                if (confirmUpdate) {
                    Car duplicate = duplicateCar.get();
                    duplicate.setAvailableCount(duplicate.getAvailableCount() + carDTO.getAvailableCount());
                    carService.updateCar(duplicate.getId(), carService.toCarDTO(duplicate));
                    response.put("message", "Duplicate car found. Increased its count.");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Duplicate car found. Update not confirmed.");
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                // Eğer başka bir araç yoksa, mevcut aracı güncelle
                carService.updateCar(id, carDTO);
                response.put("message", "Car updated successfully.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("message", "Error updating car: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    // Araç durumunu değiştirme
    @PutMapping("/cars/{id}/availability")
    public ResponseEntity<Car> updateCarAvailability(@PathVariable Long id, @RequestParam boolean available) {
        Car car = carService.updateCarAvailability(id, available);
        return ResponseEntity.ok(car);
    }

    // Siparişleri duruma göre filtrele
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersByReturnedStatus(@RequestParam Boolean returned) {
        List<Order> orders = orderService.getOrdersByReturnedStatus(returned);
        return ResponseEntity.ok(orders);
    }

    // Sipariş teslim alma
    @PutMapping("/orders/{id}/return")
    public ResponseEntity<Map<String, String>> returnOrder(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            // Siparişi al
            Order order = orderService.getOrderById(id);

            // Sipariş zaten teslim alınmışsa hata döndür
            if (order.isReturned()) {
                response.put("message", "Order is already marked as returned.");
                return ResponseEntity.badRequest().body(response);
            }

            // Siparişi teslim alındı olarak işaretle
            order.setReturned(true);

            // İlgili araç ve teslim noktası bilgilerini al
            Car car = order.getCar();
            LocationType dropoffLocation = order.getDropoffLocation();

            // Teslim edilen noktada aynı özelliklere sahip bir araç var mı kontrol et
            Optional<Car> existingCarInDropoffLocation = carService.findCarByBrandModelAndLocation(
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getColor(),
                    car.getGearType(),
                    car.getFuelType(),
                    dropoffLocation
            );

            if (existingCarInDropoffLocation.isPresent()) {
                // Araç mevcutsa, availableCount artır
                Car existingCar = existingCarInDropoffLocation.get();
                existingCar.setAvailableCount(existingCar.getAvailableCount() + 1);
                existingCar.setAvailable(true); // Araç kiralamaya uygun hale getirilir
                carService.updateCar(existingCar.getId(), carService.toCarDTO(existingCar));
            } else {
                // Araç mevcut değilse, yeni bir kayıt oluştur
                Car newCarRecord = new Car(
                        car.getBrand(),
                        car.getModel(),
                        car.getYear(),
                        car.getColor(),
                        car.getGearType(),
                        car.getFuelType(),
                        car.getKilometer(),
                        car.getDailyPrice(),
                        1, // Yeni kayıtta mevcut araç sayısı 1
                        true, // Yeni kayıt kiralamaya uygun
                        sanitizeImageUrl(car.getImageUrl()), // Görsel URL'i temizle
                        dropoffLocation
                );
                carService.createCar(carService.toCarDTO(newCarRecord), false);


            }

            // Sipariş güncellenir
            orderService.updateOrder(order);

            response.put("message", "Order marked as returned!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error processing return: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Görsel URL'yi temizleyen yardımcı metot
    private String sanitizeImageUrl(String imageUrl) {
        if (imageUrl != null) {
            if (imageUrl.startsWith("/uploads/cars//uploads/cars/")) {
                return imageUrl.replace("/uploads/cars//uploads/cars/", "/uploads/cars/");
            } else if (!imageUrl.startsWith("/uploads/cars/")) {
                return "/uploads/cars/" + imageUrl;
            }
        }
        return imageUrl;
    }


    // Araç silme
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        try {
            Car car = carService.getCarById(id); // İlgili aracı al
            CarDTO carDTO = carService.toCarDTO(car); // DTO'ya dönüştür
            return ResponseEntity.ok(carDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null); // Araç bulunamazsa 404 döner
        }
    }

}
