package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }

    public Car createCar(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setYear(carDTO.getYear());
        car.setColor(carDTO.getColor());
        car.setDailyPrice(carDTO.getDailyPrice());
        car.setAvailable(carDTO.isAvailable());
        car.setImageUrl(carDTO.getImageUrl());
        return carRepository.save(car);
    }

    public Car updateCar(Long id, CarDTO carDTO) {
        Car existingCar = getCarById(id);
        existingCar.setBrand(carDTO.getBrand());
        existingCar.setModel(carDTO.getModel());
        existingCar.setYear(carDTO.getYear());
        existingCar.setColor(carDTO.getColor());
        existingCar.setDailyPrice(carDTO.getDailyPrice());
        existingCar.setAvailable(carDTO.isAvailable());
        existingCar.setImageUrl(carDTO.getImageUrl());
        return carRepository.save(existingCar);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public Car updateCarAvailability(Long id, boolean available) {
        Car car = getCarById(id);
        car.setAvailable(available);
        return carRepository.save(car);
    }
}
