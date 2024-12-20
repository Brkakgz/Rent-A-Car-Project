package com.rentacar6.rentacar6.service;

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

    public Car createCar(Car car) {
        return carRepository.save(car);
    }

    public Car updateCar(Long id, Car carDetails) {
        Car existingCar = getCarById(id);
        existingCar.setBrand(carDetails.getBrand());
        existingCar.setModel(carDetails.getModel());
        existingCar.setYear(carDetails.getYear());
        existingCar.setColor(carDetails.getColor());
        existingCar.setDailyPrice(carDetails.getDailyPrice());
        existingCar.setAvailable(carDetails.isAvailable());
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
