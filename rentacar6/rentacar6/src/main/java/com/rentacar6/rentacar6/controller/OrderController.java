package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long customerId,
                                             @RequestParam Long carId,
                                             @RequestParam String rentDate,
                                             @RequestParam String returnDate) {
        LocalDate rent = LocalDate.parse(rentDate);
        LocalDate ret = LocalDate.parse(returnDate);
        return ResponseEntity.ok(orderService.createOrder(customerId, carId, rent, ret));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }
}
