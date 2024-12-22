package com.rentacar6.rentacar6.service;

import com.rentacar6.rentacar6.model.Customer;
import com.rentacar6.rentacar6.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Tüm müşterileri getir
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ID ile müşteri getir
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    // T.C. Kimlik Numarası (tcNo) ile müşteri getir
    public Customer getCustomerByTcNo(String tcNo) {
        return customerRepository.findByTcNo(tcNo)
                .orElseThrow(() -> new RuntimeException("Customer not found with T.C. No: " + tcNo));
    }

    // Yeni müşteri oluştur
    public Customer createCustomer(Customer customer) {
        if (!isTcNoUnique(customer.getTcNo())) {
            throw new RuntimeException("T.C. No must be unique!");
        }
        return customerRepository.save(customer);
    }

    // Mevcut müşteriyi güncelle
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer existingCustomer = getCustomerById(id);
        existingCustomer.setFirstName(customerDetails.getFirstName());
        existingCustomer.setLastName(customerDetails.getLastName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhone(customerDetails.getPhone());
        existingCustomer.setAddress(customerDetails.getAddress());
        return customerRepository.save(existingCustomer);
    }

    // Müşteriyi sil
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // T.C. Kimlik Numarasının benzersizliğini kontrol et
    public boolean isTcNoUnique(String tcNo) {
        return !customerRepository.findByTcNo(tcNo).isPresent();
    }
}
