package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.service.CarService;
import com.rentacar6.rentacar6.service.CustomerService;
import com.rentacar6.rentacar6.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // T.C. Kimlik Numarası ile kullanıcı detaylarını getir
    @GetMapping("/users/{tcNo}")
    public ResponseEntity<Customer> getUserByTcNo(@PathVariable String tcNo) {
        Customer customer = customerService.getCustomerByTcNo(tcNo);
        return ResponseEntity.ok(customer);
    }

    // Tüm kullanıcıları görüntüleme (T.C. Kimlik Numarası ile)
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
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    // Araç ekleme
    @PostMapping("/cars")
    public ResponseEntity<Car> addCar(@RequestBody CarDTO carDTO) {
        Car car = carService.createCar(carDTO);
        return ResponseEntity.ok(car);
    }

    // Araç bilgilerini güncelleme
    @PutMapping("/cars/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        Car updatedCar = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(updatedCar);
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
        return ResponseEntity.ok(carService.updateCarAvailability(id, available));
    }

    // Siparişleri duruma göre filtrele
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrdersByReturnedStatus(@RequestParam Boolean returned) {
        List<Order> orders = orderService.getOrdersByReturnedStatus(returned);
        return ResponseEntity.ok(orders);
    }

    // Sipariş teslim alma
    @PutMapping("/orders/{id}/return")
    public ResponseEntity<String> returnOrder(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            order.setReturned(true);
            orderService.updateOrder(order);
            return ResponseEntity.ok("Order marked as returned!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing return: " + e.getMessage());
        }
    }
}
