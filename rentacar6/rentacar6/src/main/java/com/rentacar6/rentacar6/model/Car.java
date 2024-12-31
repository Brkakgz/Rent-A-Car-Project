package com.rentacar6.rentacar6.model;

import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;

import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private int year;
    private String color;

    @Enumerated(EnumType.STRING)
    private GearType gearType; // Vites tipi

    @Enumerated(EnumType.STRING)
    private FuelType fuelType; // Yakıt tipi

    private int kilometer; // Araç kilometresi
    private double dailyPrice; // Günlük kira fiyatı

    @Column(name = "available_count")
    private int availableCount; // Mevcut araç sayısı
    private boolean isAvailable; // Araç kiralamaya uygun mu
    private String imageUrl; // Görsel URL

    @Enumerated(EnumType.STRING)
    private LocationType location; // Aracın bulunduğu lokasyon

    // Default Constructor
    public Car() {}

    public GearType getGearType() {
        return gearType;
    }

    public void setGearType(GearType gearType) {
        this.gearType = gearType;
    }

    public int getKilometer() {
        return kilometer;
    }

    public void setKilometer(int kilometer) {
        this.kilometer = kilometer;
    }

    // Parametreli Constructor
    public Car(String brand, String model, int year, String color, GearType gearType, FuelType fuelType,
               int kilometer, double dailyPrice, int availableCount, boolean isAvailable, String imageFileName, LocationType location) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.gearType = gearType;
        this.fuelType = fuelType;
        this.kilometer = kilometer;
        this.dailyPrice = dailyPrice;
        this.availableCount = availableCount;
        this.isAvailable = isAvailable;
        this.imageUrl = "/uploads/cars/" + imageFileName;
        this.location = location;
    }

    // Getter and Setter Metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }



    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocationType getLocation() {
        return location;
    }

    public void setLocation(LocationType location) {
        this.location = location;
    }
}
