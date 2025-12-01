package com.bibliotek.personal.controller;

import com.bibliotek.personal.dto.ExchangeDTO;
import com.bibliotek.personal.dto.ExchangeRequestDTO;
import com.bibliotek.personal.dto.ExchangeStatusUpdateDTO;
import com.bibliotek.personal.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/request")
    public ResponseEntity<ExchangeDTO> requestExchange(@RequestBody ExchangeRequestDTO exchangeRequest) {
        ExchangeDTO exchange = exchangeService.requestExchange(exchangeRequest);
        return new ResponseEntity<>(exchange, HttpStatus.CREATED);
    }


    @PutMapping("/{exchangeId}/status")
    public ResponseEntity<ExchangeDTO> updateExchangeStatus(
            @PathVariable int exchangeId,
            @RequestBody ExchangeStatusUpdateDTO statusUpdate) {
        ExchangeDTO updatedExchange = exchangeService.updateExchangeStatus(exchangeId, statusUpdate.getStatus());
        return new ResponseEntity<>(updatedExchange, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ExchangeDTO>> getUserExchanges() {
        List<ExchangeDTO> exchanges = exchangeService.getExchangesForLoggedInUser();
        return new ResponseEntity<>(exchanges, HttpStatus.OK);
    }

}