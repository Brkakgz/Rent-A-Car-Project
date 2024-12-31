package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.enums.LocationType;
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

    @Autowired
    private HistoryService historyService;



    // Sipariş oluşturma (Müşteri ID ve Araç ID ile)
    public Order createOrder(Long customerId, Long carId, LocalDate rentDate, LocalDate returnDate, String pickupLocation, String dropoffLocation) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + carId));

        // Araç uygun değilse hata döndür
        if (!car.isAvailable() || car.getAvailableCount() <= 0) {
            throw new RuntimeException("The selected car is currently unavailable.");
        }

        // Tarihlerin geçerli olup olmadığını kontrol et
        if (rentDate.isAfter(returnDate) || rentDate.isEqual(returnDate)) {
            throw new RuntimeException("Invalid rent or return date.");
        }

        // Toplam fiyatı hesapla
        long days = ChronoUnit.DAYS.between(rentDate, returnDate);
        double totalPrice = days * car.getDailyPrice();

        // Lokasyon doğrulaması
        LocationType pickupEnumLocation = LocationType.valueOf(pickupLocation.toUpperCase());
        LocationType dropoffEnumLocation = LocationType.valueOf(dropoffLocation.toUpperCase());

        // Sipariş oluştur
        Order order = new Order();
        order.setCustomer(customer);
        order.setCar(car);
        order.setRentDate(rentDate);
        order.setReturnDate(returnDate);
        order.setPickupLocation(pickupEnumLocation); // Enum set ediliyor
        order.setDropoffLocation(dropoffEnumLocation); // Enum set ediliyor
        order.setTotalPrice(totalPrice);
        order.setReturned(false);

        // Araç sayısını azalt
        car.setAvailableCount(car.getAvailableCount() - 1);
        if (car.getAvailableCount() == 0) {
            car.setAvailable(false); // Tüm araçlar kiralanmışsa uygunluğu kaldır
        }
        carRepository.save(car);

        // Siparişi kaydet
        Order savedOrder = orderRepository.save(order);

        // History kaydı yap
        historyService.saveHistory(savedOrder);

        return savedOrder;
    }

    // Araç teslim alma
    public void markOrderAsReturned(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.isReturned()) {
            throw new RuntimeException("Order is already marked as returned.");
        }

        // Siparişi teslim edildi olarak işaretle
        order.setReturned(true);

        // Araç sayısını artır
        Car car = order.getCar();
        car.setAvailableCount(car.getAvailableCount() + 1);
        car.setAvailable(true); // Araç tekrar kullanılabilir hale gelir
        carRepository.save(car);

        orderRepository.save(order);
    }


    // Belirli bir müşteriye ait siparişleri getir
    public List<Order> getOrdersByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
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

    // Teslim edilen siparişleri listele
    public List<Order> getReturnedOrders() {
        return orderRepository.findByReturned(true);
    }

    // Teslim edilmeyen siparişleri listele
    public List<Order> getUnreturnedOrders() {
        return orderRepository.findByReturned(false);
    }

    // Teslim durumu ile siparişleri listele
    public List<Order> getOrdersByReturnedStatus(Boolean returned) {
        return orderRepository.findByReturned(returned);
    }

    // Sipariş silme
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);

        // Araç tekrar kullanılabilir hale getiriliyor
        Car car = order.getCar();
        car.setAvailableCount(car.getAvailableCount() + 1);
        car.setAvailable(true);
        carRepository.save(car);

        // Sipariş siliniyor
        orderRepository.deleteById(orderId);
    }
}
