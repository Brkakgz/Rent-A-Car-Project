package com.rentacar6.rentacar6.dto;

public class CreateOrderRequest {
    private Long carId; // Kiralanacak aracın ID'si
    private String rentDate; // Kiralama tarihi
    private String returnDate; // Teslim tarihi
    private String pickupLocation; // Kiralama adresi
    private String dropoffLocation; // Teslim adresi

    // Getter ve Setter metotları
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getRentDate() {
        return rentDate;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
}
