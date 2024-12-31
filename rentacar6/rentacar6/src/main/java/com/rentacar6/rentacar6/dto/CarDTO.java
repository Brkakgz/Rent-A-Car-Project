package com.rentacar6.rentacar6.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarDTO {

    private Long id; // ID Alanı
    private String brand; // Marka
    private String model; // Model
    private int year; // Yıl
    private String color; // Renk
    private String gearType; // Vites Tipi (MANUAL veya AUTOMATIC)
    private String fuelType; // Yakıt Tipi (PETROL, DIESEL, ELECTRIC)

    @JsonProperty("kilometer")
    private int kilometers; // Kilometre
    private double dailyPrice; // Günlük Kira Bedeli
    private boolean available; // Mevcut mu?
    private String location; // Mevcut Lokasyon (ISTANBUL, ANKARA gibi)
    private String imageUrl; // Görsel URL
    private int availableCount; // Mevcut Araç Sayısı (Eklenen alan)

    // Parametreli Constructor
    public CarDTO(Long id, String brand, String model, int year, String color, String gearType,
                  String fuelType, int kilometers, double dailyPrice, boolean available,
                  String location, String imageUrl, int availableCount) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.gearType = gearType;
        this.fuelType = fuelType;
        this.kilometers = kilometers;
        this.dailyPrice = dailyPrice;
        this.available = available;
        this.location = location;
        this.imageUrl = imageUrl;
        this.availableCount = availableCount; // Mevcut Araç Sayısını Ata
    }

    // Getter ve Setter Metotları
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

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    @Override
    public String toString() {
        return "CarDTO{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", dailyPrice=" + dailyPrice +
                ", availableCount=" + availableCount +
                ", available=" + available +
                ", gearType='" + gearType + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", kilometer=" + kilometers +
                ", location='" + location + '\'' +
                '}';
    }

}

