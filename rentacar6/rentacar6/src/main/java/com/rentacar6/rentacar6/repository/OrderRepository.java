package com.rentacar6.rentacar6.repository;

import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(Customer customer);
    List<Order> findByReturned(boolean returned);
    List<Order> findByReturned(Boolean returned);
}
