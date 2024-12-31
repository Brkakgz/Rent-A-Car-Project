package com.rentacar6.rentacar6.repository;

import com.rentacar6.rentacar6.enums.LocationType;
import com.rentacar6.rentacar6.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByBrandAndModelAndLocation(String brand, String model, LocationType location);

}
