package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.dto.CarDTO;
import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;
import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    // Tüm araçları getir
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // ID ile araç getir
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }

    // Yeni araç oluştur
    public Car createCar(CarDTO carDTO, boolean confirmUpdate) {
        Optional<Car> existingCar = findCarByMatchingDetails(
                carDTO.getBrand(),
                carDTO.getModel(),
                carDTO.getYear(),
                carDTO.getColor(),
                GearType.valueOf(carDTO.getGearType().toUpperCase()),
                FuelType.valueOf(carDTO.getFuelType().toUpperCase()),
                LocationType.valueOf(carDTO.getLocation().toUpperCase()),
                carDTO.getDailyPrice()
        );

        if (existingCar.isPresent()) {
            if (confirmUpdate) {
                // Eğer onay verilmişse mevcut kaydı güncelle
                Car car = existingCar.get();
                car.setAvailableCount(car.getAvailableCount() + carDTO.getAvailableCount());
                car.setAvailable(true);
                return carRepository.save(car); // Güncelleme işlemi
            } else {
                // Eğer onay verilmemişse işlem uygulanmaz
                throw new RuntimeException("Operation cancelled by the user.");
            }
        } else {
            // Yeni kayıt oluştur
            Car car = new Car();
            updateCarFromDTO(car, carDTO); // DTO'dan Car nesnesine kopyalama
            return carRepository.save(car); // Yeni kayıt işlemi
        }
    }



    public Optional<Car> findCarByMatchingDetails(String brand, String model, int year, String color,
                                                  GearType gearType, FuelType fuelType,
                                                  LocationType location, double dailyPrice) {
        return carRepository.findAll().stream()
                .filter(car -> car.getBrand().equalsIgnoreCase(brand))
                .filter(car -> car.getModel().equalsIgnoreCase(model))
                .filter(car -> car.getYear() == year)
                .filter(car -> car.getColor().equalsIgnoreCase(color))
                .filter(car -> car.getGearType() == gearType)
                .filter(car -> car.getFuelType() == fuelType)
                .filter(car -> car.getLocation() == location)
                .filter(car -> car.getDailyPrice() == dailyPrice)
                .findFirst();
    }


    // Mevcut aracı güncelle
    public Car updateCar(Long id, CarDTO carDTO) {
        Car existingCar = getCarById(id);
        updateCarFromDTO(existingCar, carDTO); // DTO'dan Car nesnesine güncelleme
        return carRepository.save(existingCar);
    }

    // Araç sil
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    // Araç durumunu güncelle
    public Car updateCarAvailability(Long id, boolean available) {
        Car car = getCarById(id);
        car.setAvailable(available);
        return carRepository.save(car);
    }

    // Filtrelenmiş araçları getir
    public List<CarDTO> getFilteredCars(String brand, String model, String color, Double minPrice, Double maxPrice,
                                        Integer year, String gearType, String fuelType, String location,
                                        Integer minKilometers, Integer maxKilometers) {
        List<Car> cars = carRepository.findAll(); // Tüm araçları al
        return cars.stream()
                .filter(car -> (brand == null || brand.isBlank() || car.getBrand().equalsIgnoreCase(brand)))
                .filter(car -> (model == null || model.isBlank() || car.getModel().equalsIgnoreCase(model)))
                .filter(car -> (color == null || color.isBlank() || car.getColor().equalsIgnoreCase(color)))
                .filter(car -> (minPrice == null || car.getDailyPrice() >= minPrice))
                .filter(car -> (maxPrice == null || car.getDailyPrice() <= maxPrice))
                .filter(car -> (year == null || car.getYear() == year))
                .filter(car -> (gearType == null || gearType.isBlank() || car.getGearType().toString().equalsIgnoreCase(gearType)))
                .filter(car -> (fuelType == null || fuelType.isBlank() || car.getFuelType().toString().equalsIgnoreCase(fuelType)))
                .filter(car -> (location == null || location.isBlank() || car.getLocation().toString().equalsIgnoreCase(location)))
                .filter(car -> (minKilometers == null || car.getKilometer() >= minKilometers))
                .filter(car -> (maxKilometers == null || car.getKilometer() <= maxKilometers))
                .map(this::toCarDTO) // Car -> CarDTO dönüşümü
                .collect(Collectors.toList());
    }


    // Yardımcı: Car -> CarDTO dönüşümü
    public CarDTO toCarDTO(Car car) {
        return new CarDTO(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getGearType().toString(),
                car.getFuelType().toString(),
                car.getKilometer(),
                car.getDailyPrice(),
                car.isAvailable(),
                car.getLocation().toString(),
                temizleGorselYolu(car.getImageUrl()), // Görsel yolu temizle
                car.getAvailableCount()
        );
    }


    private String temizleGorselYolu(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "/uploads/cars/default.jpg"; // Varsayılan görsel
        }
        if (imageUrl.startsWith("/uploads/cars//uploads/cars/")) {
            return imageUrl.replace("/uploads/cars//uploads/cars/", "/uploads/cars/");
        }
        if (!imageUrl.startsWith("/uploads/cars/")) {
            return "/uploads/cars/" + imageUrl;
        }
        return imageUrl;
    }


    // Yardımcı: DTO'dan Car'a veri kopyalama
    private void updateCarFromDTO(Car car, CarDTO carDTO) {

        System.out.println("CarDTO Kilometer in updateCarFromDTO: " + carDTO.getKilometers()); // Gelen veriyi kontrol et
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setYear(carDTO.getYear());
        car.setColor(carDTO.getColor());
        car.setGearType(GearType.valueOf(carDTO.getGearType().toUpperCase()));
        car.setFuelType(FuelType.valueOf(carDTO.getFuelType().toUpperCase()));
        car.setKilometer(carDTO.getKilometers());
        car.setDailyPrice(carDTO.getDailyPrice());
        car.setAvailable(carDTO.isAvailable());
        car.setAvailableCount(carDTO.getAvailableCount());
        car.setLocation(LocationType.valueOf(carDTO.getLocation().toUpperCase()));
        car.setImageUrl(temizleGorselYolu(carDTO.getImageUrl())); // Görsel yolu temizle
    }


    // Marka, model ve lokasyona göre araç arama
    public Optional<Car> findCarByBrandModelAndLocation(String brand, String model, int year, String color,
                                                        GearType gearType, FuelType fuelType, LocationType location) {
        return carRepository.findAll().stream()
                .filter(car -> car.getBrand().equalsIgnoreCase(brand))
                .filter(car -> car.getModel().equalsIgnoreCase(model))
                .filter(car -> car.getYear() == year)
                .filter(car -> car.getColor().equalsIgnoreCase(color))
                .filter(car -> car.getGearType() == gearType)
                .filter(car -> car.getFuelType() == fuelType)
                .filter(car -> car.getLocation() == location)
                .findFirst();
    }


    public Car incrementCarCount(Long id, int countToAdd) {
        Car car = getCarById(id);
        car.setAvailableCount(car.getAvailableCount() + countToAdd);
        return carRepository.save(car);
    }



        public List<Car> getCarsByLocation(LocationType locationType) {
            return carRepository.findByLocation(locationType);
        }

}
