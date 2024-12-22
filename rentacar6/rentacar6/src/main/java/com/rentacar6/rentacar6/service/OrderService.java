package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.repository.CarRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import com.rentacar6.rentacar6.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Sipariş oluşturma (T.C. Kimlik No ile)
    public Order createOrder(String tcNo, Long carId, LocalDate rentDate, LocalDate returnDate) {
        Customer customer = customerRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new RuntimeException("Customer not found with T.C. No: " + tcNo));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + carId));

        if (!car.isAvailable()) {
            throw new RuntimeException("Car is not available for rent: " + carId);
        }

        long days = ChronoUnit.DAYS.between(rentDate, returnDate);
        double totalPrice = days * car.getDailyPrice();

        car.setAvailable(false);
        carRepository.save(car);

        Order order = new Order(customer, car, rentDate, returnDate, totalPrice);
        return orderRepository.save(order);
    }

    // Belirli bir müşteriye ait siparişleri getir
    public List<Order> getOrdersByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return orderRepository.findByCustomer(customer);
    }

    // T.C. Kimlik No ile siparişleri getir
    public List<Order> getOrdersByTcNo(String tcNo) {
        Customer customer = customerRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new RuntimeException("Customer not found with T.C. No: " + tcNo));
        return orderRepository.findByCustomer(customer);
    }

    // Tüm siparişleri getir
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Siparişi güncelle
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    // Sipariş ID ile getir
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    // Teslim edilen siparişleri listeleme
    public List<Order> getReturnedOrders() {
        return orderRepository.findByReturned(true);
    }

    // Teslim edilmeyen siparişleri listeleme
    public List<Order> getUnreturnedOrders() {
        return orderRepository.findByReturned(false);
    }

    // Teslim durumu ile siparişleri listeleme
    public List<Order> getOrdersByReturnedStatus(Boolean returned) {
        return orderRepository.findByReturned(returned);
    }

    // Sipariş silme
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);

        // Araç tekrar kiralanabilir hale getiriliyor
        Car car = order.getCar();
        car.setAvailable(true);
        carRepository.save(car);

        // Sipariş siliniyor
        orderRepository.deleteById(orderId);
    }
}
