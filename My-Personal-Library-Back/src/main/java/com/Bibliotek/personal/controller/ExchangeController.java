package com.Bibliotek.personal.controller;

import com.Bibliotek.personal.dto.ExchangeDTO;
import com.Bibliotek.personal.dto.ExchangeRequestDTO;
import com.Bibliotek.personal.dto.ExchangeStatusUpdateDTO;
import com.Bibliotek.personal.service.ExchangeService;
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

    @PostMapping("/request")
    public ResponseEntity<ExchangeDTO> requestExchange(@RequestBody ExchangeRequestDTO exchangeRequest) {
        ExchangeDTO dto = exchangeService.requestExchange(exchangeRequest);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{exchangeId}/status")
    public ResponseEntity<ExchangeDTO> updateExchangeStatus(
            @PathVariable int exchangeId,
            @RequestBody ExchangeStatusUpdateDTO statusUpdate) {
        ExchangeDTO dto = exchangeService.updateExchangeStatus(exchangeId, statusUpdate.getStatus());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExchangeDTO>> getUserExchanges() {
        List<ExchangeDTO> exchanges = exchangeService.getExchangesForLoggedInUser();
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }

    @GetMapping("/borrowed")
    public ResponseEntity<List<ExchangeDTO>> getBorrowedBooks() {
        List<ExchangeDTO> exchanges = exchangeService.getExchangesForLoggedInUser();
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }

    @GetMapping("/lending")
    public ResponseEntity<List<ExchangeDTO>> getLendingBooks() {
        List<ExchangeDTO> exchanges = exchangeService.getLendingExchangesForLoggedInUser();
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }

    @DeleteMapping("/{exchangeId}")
    public ResponseEntity<Void> deleteExchange(@PathVariable int exchangeId) {
        exchangeService.deleteExchange(exchangeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}