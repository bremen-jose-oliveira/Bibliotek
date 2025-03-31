package com.bibliotek.personal.controller;

import com.bibliotek.personal.dto.ExchangeRequestDTO;
import com.bibliotek.personal.dto.ExchangeStatusUpdateDTO;
import com.bibliotek.personal.entity.Exchange;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.exception.ResourceNotFoundException;
import com.bibliotek.personal.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<Exchange> requestExchange(@RequestBody ExchangeRequestDTO exchangeRequest) {
        Exchange exchange = exchangeService.requestExchange(exchangeRequest);
        return new ResponseEntity<>(exchange, HttpStatus.CREATED);
    }


    @PutMapping("/{exchangeId}/status")
    public ResponseEntity<Exchange> updateExchangeStatus(
            @PathVariable int exchangeId,
            @RequestBody ExchangeStatusUpdateDTO statusUpdate) {
        Exchange updatedExchange = exchangeService.updateExchangeStatus(exchangeId, statusUpdate.getStatus());
        return new ResponseEntity<>(updatedExchange, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Exchange>> getUserExchanges() {
        List<Exchange> exchanges = exchangeService.getExchangesForLoggedInUser();
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }

}