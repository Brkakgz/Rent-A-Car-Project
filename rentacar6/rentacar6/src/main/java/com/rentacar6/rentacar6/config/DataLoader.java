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
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin ve Kullanıcı Oluşturma
        createAdminAndCustomer();

        // Araçları ekle
        addCarsWithValidation();

        System.out.println("Data loaded successfully!");
    }

    private void createAdminAndCustomer() {
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
        customer.setPhone("9876543219");
        customer.setTcNo(userUniqueTcNo);
        customer.setAddress("456 Customer St");
        customer.setPassword(passwordEncoder.encode("user123"));
        customer.setRole("ROLE_USER");
        customerRepository.save(customer);
    }

    private void addCarsWithValidation() {
        addCar("Toyota", "Corolla", 2020, "White", GearType.AUTOMATIC, FuelType.GASOLINE, 15000, 50.0, 10, "Toyota-Corolla-2020-White.jpg", LocationType.ISTANBUL);
        addCar("Tesla", "Model 3", 2021, "Red", GearType.AUTOMATIC, FuelType.ELECTRIC, 5000, 70.0, 7, "Tesla-Model3-2021-Red.jpg", LocationType.ISTANBUL);
        addCar("Honda", "Civic", 2019, "Blue", GearType.MANUAL, FuelType.DIESEL, 20000, 45.0, 8, "Honda-Civic-2019-Blue.jpg", LocationType.ANKARA);
        addCar("TOGG", "T10X", 2023, "Black", GearType.AUTOMATIC, FuelType.ELECTRIC, 3000, 80.0, 5, "TOGG-T10X-2023-Black.jpg", LocationType.ANKARA);
        addCar("Ford", "Focus", 2021, "Black", GearType.AUTOMATIC, FuelType.ELECTRIC, 10000, 60.0, 5, "Ford-Focus-2021-Black.jpg", LocationType.IZMIR);
        addCar("BMW", "iX3", 2022, "Silver", GearType.AUTOMATIC, FuelType.ELECTRIC, 7000, 90.0, 6, "BMW-iX3-2022-Silver.jpg", LocationType.IZMIR);
        addCar("Mercedes", "EQS", 2022, "White", GearType.AUTOMATIC, FuelType.ELECTRIC, 1000, 120.0, 3, "Mercedes-EQS-2022-White.jpg", LocationType.BURSA);
        addCar("Audi", "e-Tron", 2022, "Blue", GearType.AUTOMATIC, FuelType.ELECTRIC, 2000, 100.0, 4, "Audi-eTron-2022-Blue.jpg", LocationType.BURSA);
        addCar("Hyundai", "Ioniq 5", 2022, "Gray", GearType.AUTOMATIC, FuelType.ELECTRIC, 3000, 70.0, 6, "Hyundai-Ioniq5-2022-Gray.jpg", LocationType.ANTALYA);
        addCar("Kia", "EV6", 2022, "Black", GearType.AUTOMATIC, FuelType.ELECTRIC, 2500, 75.0, 5, "Kia-EV6-2022-Black.jpg", LocationType.ANTALYA);
    }

    private void addCar(String brand, String model, int year, String color, GearType gearType, FuelType fuelType, int kilometer, double dailyPrice, int availableCount, String imageUrl, LocationType location) {
        // GearType ve FuelType enumlarını doğrudan kullanın
        boolean exists = carRepository.existsByBrandAndModelAndYearAndColorAndGearTypeAndFuelTypeAndLocation(
                brand,
                model,
                year,
                color,
                gearType,
                fuelType,
                location
        );

        if (!exists) {
            Car car = new Car(brand, model, year, color, gearType, fuelType, kilometer, dailyPrice, availableCount, true, imageUrl, location);
            carRepository.save(car);
        } else {
            System.out.println("Car already exists: " + brand + " " + model);
        }
    }


    private String generateUniqueTcNo() {
        String tcNoFirst10 = String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));
        int lastDigit = ThreadLocalRandom.current().nextInt(0, 5) * 2;
        return tcNoFirst10 + lastDigit;
    }
}
