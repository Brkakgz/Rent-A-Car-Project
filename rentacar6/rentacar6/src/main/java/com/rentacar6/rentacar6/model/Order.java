package com.rentacar6.rentacar6.model;

import com.rentacar6.rentacar6.enums.LocationType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private LocalDate rentDate; // Kiralama tarihi
    private LocalDate returnDate; // Teslim tarihi

    private double totalPrice; // Toplam kira ücreti

    @Enumerated(EnumType.STRING)
    private LocationType pickupLocation; // Teslim alma lokasyonu

    @Enumerated(EnumType.STRING)
    private LocationType dropoffLocation; // Teslim etme lokasyonu

    @Column(nullable = false)
    private boolean returned; // Siparişin teslim edilip edilmediğini takip eder

    public Order() {}

    public Order(Customer customer, Car car, LocalDate rentDate, LocalDate returnDate, double totalPrice,
                 LocationType pickupLocation, LocationType dropoffLocation) {
        this.customer = customer;
        this.car = car;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.totalPrice = totalPrice;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.returned = false; // Yeni siparişler varsayılan olarak teslim edilmemiş olur
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
