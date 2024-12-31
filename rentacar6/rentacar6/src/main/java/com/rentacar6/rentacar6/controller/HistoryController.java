package com.rentacar6.rentacar6.controller;

import com.rentacar6.rentacar6.model.History;
import com.rentacar6.rentacar6.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")

public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<History>> getAllHistory() {
        List<History> historyList = historyService.getAllHistory();
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/customer/{tcNo}")
    public ResponseEntity<List<History>> getCustomerHistory(@PathVariable String tcNo) {
        List<History> customerHistory = historyService.getHistoryByCustomerTcNo(tcNo);
        return ResponseEntity.ok(customerHistory);
    }
}

