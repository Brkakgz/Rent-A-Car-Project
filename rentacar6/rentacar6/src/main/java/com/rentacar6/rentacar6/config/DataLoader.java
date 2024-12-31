package com.rentacar6.rentacar6.config;

import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;
import com.rentacar6.rentacar6.model.Admin;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.AdminRepository;
import com.rentacar6.rentacar6.repository.CarRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import com.rentacar6.rentacar6.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private OrderService orderService; // OrderService kullanımı

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin and customer users
        String adminUniqueEmail = "admin" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String userUniqueEmail = "user" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        String adminUniqueTcNo = generateUniqueTcNo();
        String userUniqueTcNo = generateUniqueTcNo();

        Admin admin = new Admin();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail(adminUniqueEmail);
        admin.setPhone("123456789");
        admin.setTcNo(adminUniqueTcNo);
        admin.setAddress("123 Admin St");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        adminRepository.save(admin);

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail(userUniqueEmail);
        customer.setPhone("987654321");
        customer.setTcNo(userUniqueTcNo);
        customer.setAddress("456 Customer St");
        customer.setPassword(passwordEncoder.encode("user123"));
        customer.setRole("ROLE_USER");
        customerRepository.save(customer);

        // Add cars
        Car car1 = new Car("Toyota", "Corolla", 2020, "White", GearType.AUTOMATIC, FuelType.GASOLINE, 15000, 50.0, 10, true, "Toyota-Corolla-2020-White.jpg", LocationType.ISTANBUL);
        Car car2 = new Car("Honda", "Civic", 2019, "Blue", GearType.MANUAL, FuelType.DIESEL, 20000, 45.0, 8, true, "Honda-Civic-2019-Blue.jpg", LocationType.ANKARA);
        Car car3 = new Car("Ford", "Focus", 2021, "Black", GearType.AUTOMATIC, FuelType.ELECTRIC, 10000, 60.0, 5, true, "Ford-Focus-2021-Black.jpg", LocationType.IZMIR);

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);

        // Add orders using OrderService
        orderService.createOrder(
                customer.getId(),
                car1.getId(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 10),
                LocationType.ISTANBUL.name(),
                LocationType.ANKARA.name()
        );

        orderService.createOrder(
                customer.getId(),
                car2.getId(),
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 10),
                LocationType.ANKARA.name(),
                LocationType.IZMIR.name()
        );

        System.out.println("Data loaded successfully!");
    }

    private String generateUniqueTcNo() {
        String tcNoFirst10 = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));
        int lastDigit = ThreadLocalRandom.current().nextInt(0, 5) * 2;
        return tcNoFirst10 + lastDigit;
    }
}
