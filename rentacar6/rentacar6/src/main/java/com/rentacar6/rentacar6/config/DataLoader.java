package com.rentacar6.rentacar6.config;

import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.CarRepository;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin Kullanıcı
        Customer admin = new Customer("Admin", "User", "admin@example.com", "123456789", "123 Admin St", "11111111110", passwordEncoder.encode("admin123"));
        customerRepository.save(admin);

        // Kullanıcı
        Customer user = new Customer("John", "Doe", "user@example.com", "987654321", "456 User St", "22222222220", passwordEncoder.encode("user123"));
        customerRepository.save(user);

        // Araçlar
        Car car1 = new Car("Toyota", "Corolla", 2020, "White", 50.0, true);
        Car car2 = new Car("Honda", "Civic", 2019, "Black", 45.0, true);
        carRepository.save(car1);
        carRepository.save(car2);

        System.out.println("Data loaded successfully!");
    }
}
