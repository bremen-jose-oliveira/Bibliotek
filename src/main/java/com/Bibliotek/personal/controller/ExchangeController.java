package com.bibliotek.personal.controller;

import com.bibliotek.personal.entity.Exchange;
import com.bibliotek.personal.exception.ResourceNotFoundException;
import com.bibliotek.personal.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/request/{borrowerId}/{bookId}")
    public ResponseEntity<Exchange> requestExchange(@PathVariable int borrowerId, @PathVariable int bookId) {
        Exchange exchange = exchangeService.requestExchange(borrowerId, bookId);
        return new ResponseEntity<>(exchange, HttpStatus.CREATED);
    }

    @PutMapping("/{exchangeId}/status")
    public ResponseEntity<Exchange> updateExchangeStatus(@PathVariable int exchangeId, @RequestParam Exchange.ExchangeStatus status) {
        Exchange updatedExchange = exchangeService.updateExchangeStatus(exchangeId, status);
        return new ResponseEntity<>(updatedExchange, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Exchange>> getUserExchanges(@PathVariable int userId) {
        List<Exchange> exchanges = exchangeService.getExchangesByUser(userId);
        if (exchanges.isEmpty()) {
            throw new ResourceNotFoundException("No exchanges found for user ID: " + userId);
        }
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }
}