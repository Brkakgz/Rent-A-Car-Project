package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.dto.CreateOrderRequest;
import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.service.OrderService;
import com.rentacar6.rentacar6.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Sipariş oluşturma
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest orderRequest) {
        // Giriş yapan kullanıcının kimliğini alın
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.getCustomerByEmail(email).getId();

        // Tarihleri Parse Et
        LocalDate rentDate = LocalDate.parse(orderRequest.getRentDate());
        LocalDate returnDate = LocalDate.parse(orderRequest.getReturnDate());

        // Sipariş oluştur ve geri döndür
        try {
            Order createdOrder = orderService.createOrder(
                    customerId,
                    orderRequest.getCarId(),
                    rentDate,
                    returnDate,
                    orderRequest.getPickupLocation(),
                    orderRequest.getDropoffLocation()
            );
            return ResponseEntity.ok(createdOrder);
        } catch (RuntimeException e) {
            // Hataları yakalayıp uygun HTTP yanıtını döndür
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Giriş yapan müşterinin sipariş geçmişini görüntüleme
    @GetMapping("/customer/history")
    public ResponseEntity<List<Order>> getCustomerOrderHistory() {
        // Giriş yapan kullanıcının kimliğini alın
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long customerId = customerService.getCustomerByEmail(email).getId();

        // Müşterinin sipariş geçmişini al ve döndür
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
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
