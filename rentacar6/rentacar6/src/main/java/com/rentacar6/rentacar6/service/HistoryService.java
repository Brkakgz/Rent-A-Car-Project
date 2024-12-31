package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.model.Car;
import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.model.History;
import com.rentacar6.rentacar6.model.Order;
import com.rentacar6.rentacar6.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    public void saveHistory(Order order) {
        History history = new History();

        Customer customer = order.getCustomer();
        history.setTcNo(customer.getTcNo());
        history.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

        Car car = order.getCar();
        history.setCarBrand(car.getBrand());
        history.setCarModel(car.getModel());
        history.setCarYear(car.getYear());
        history.setCarGearType(car.getGearType()); // Enum türü doğrudan atanır
        history.setCarFuelType(car.getFuelType()); // Enum türü doğrudan atanır
        history.setCarColor(car.getColor());

        history.setPickupLocation(order.getPickupLocation()); // Enum doğrudan atanabilir
        history.setDropoffLocation(order.getDropoffLocation()); // Enum doğrudan atanabilir
        history.setRentDate(order.getRentDate());
        history.setReturnDate(order.getReturnDate());
        history.setReturned(order.isReturned());
        history.setTotalPrice(order.getTotalPrice());

        historyRepository.save(history);
    }

    public List<History> getAllHistory() {
        return historyRepository.findAllByOrderByRentDateDesc();
    }
}


