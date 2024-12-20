package com.rentacar6.rentacar6.repository;

import com.rentacar6.rentacar6.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {}
