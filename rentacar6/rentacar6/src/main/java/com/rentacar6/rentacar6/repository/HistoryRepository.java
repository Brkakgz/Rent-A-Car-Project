package com.rentacar6.rentacar6.repository;

import com.rentacar6.rentacar6.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByOrderByRentDateDesc(); // Tarihe göre sıralama
}
