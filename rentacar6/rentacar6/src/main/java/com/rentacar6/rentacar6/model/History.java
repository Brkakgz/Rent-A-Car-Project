package com.rentacar6.rentacar6.model;

import com.rentacar6.rentacar6.enums.FuelType;
import com.rentacar6.rentacar6.enums.GearType;
import com.rentacar6.rentacar6.enums.LocationType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tcNo;
    private String customerName;

    private String carBrand;
    private String carModel;
    private int carYear;

    @Enumerated(EnumType.STRING)
    private GearType carGearType; // Enum olarak güncellendi

    @Enumerated(EnumType.STRING)
    private FuelType carFuelType; // Enum olarak güncellendi

    private String carColor;

    @Enumerated(EnumType.STRING)
    private LocationType pickupLocation;

    @Enumerated(EnumType.STRING)
    private LocationType dropoffLocation;

    private LocalDate rentDate;
    private LocalDate returnDate;

    private boolean returned;
    private double totalPrice;

    // Getter ve Setter Metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTcNo() {
        return tcNo;
    }

    public void setTcNo(String tcNo) {
        this.tcNo = tcNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public GearType getCarGearType() {
        return carGearType;
    }

    public void setCarGearType(GearType carGearType) {
        this.carGearType = carGearType;
    }

    public FuelType getCarFuelType() {
        return carFuelType;
    }

    public void setCarFuelType(FuelType carFuelType) {
        this.carFuelType = carFuelType;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public LocationType getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(LocationType pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public LocationType getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(LocationType dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
