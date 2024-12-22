package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.service.OrderService;
import com.rentacar6.rentacar6.service.CustomerService;
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

    @Autowired
    private CustomerService customerService;

    // T.C. Kimlik Numarası ile sipariş oluşturma
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam String tcNo,
                                             @RequestParam Long carId,
                                             @RequestParam String rentDate,
                                             @RequestParam String returnDate) {
        LocalDate rent = LocalDate.parse(rentDate);
        LocalDate ret = LocalDate.parse(returnDate);
        Long customerId = customerService.getCustomerByTcNo(tcNo).getId(); // T.C. ile müşteri ID'sini al
        return ResponseEntity.ok(orderService.createOrder(tcNo, carId, rent, ret));
    }

    // Belirli bir müşterinin siparişlerini görüntüleme (T.C. Kimlik Numarası ile)
    @GetMapping("/{tcNo}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String tcNo) {
        Long customerId = customerService.getCustomerByTcNo(tcNo).getId(); // T.C. ile müşteri ID'sini al
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // Giriş yapan müşterinin sipariş geçmişini görüntüleme
    @GetMapping("/history/{tcNo}")
    public ResponseEntity<List<Order>> getOrderHistory(@PathVariable String tcNo) {
        Long customerId = customerService.getCustomerByTcNo(tcNo).getId(); // T.C. ile müşteri ID'sini al
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // Teslim edilen siparişleri listeleme
    @GetMapping("/returned")
    public ResponseEntity<List<Order>> getReturnedOrders() {
        return ResponseEntity.ok(orderService.getReturnedOrders());
    }

    // Teslim edilmeyen siparişleri listeleme
    @GetMapping("/unreturned")
    public ResponseEntity<List<Order>> getUnreturnedOrders() {
        return ResponseEntity.ok(orderService.getUnreturnedOrders());
    }

    // Sipariş durumuna göre filtreleme
    @GetMapping("/filter")
    public ResponseEntity<List<Order>> filterOrders(@RequestParam(required = false) Boolean returned) {
        List<Order> orders;
        if (returned == null) {
            orders = orderService.getAllOrders(); // Tüm siparişler
        } else {
            orders = orderService.getOrdersByReturnedStatus(returned); // Teslim edilen veya edilmeyen siparişler
        }
        return ResponseEntity.ok(orders);
    }
}
