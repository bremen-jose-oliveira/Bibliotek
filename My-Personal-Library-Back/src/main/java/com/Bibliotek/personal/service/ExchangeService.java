package com.Bibliotek.personal.service;


import com.Bibliotek.personal.dto.ExchangeRequestDTO;
import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Exchange;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.exception.ResourceNotFoundException;
import com.Bibliotek.personal.repository.BookRepository;
import com.Bibliotek.personal.repository.ExchangeRepository;
import com.Bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }


    public Exchange requestExchange(ExchangeRequestDTO exchangeRequest) {
        User borrower = userRepository.findById(exchangeRequest.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + exchangeRequest.getBorrowerId()));

        Book book = bookRepository.findById(exchangeRequest.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + exchangeRequest.getBookId()));

        Exchange newExchange = new Exchange();
        newExchange.setBorrower(borrower);
        newExchange.setBook(book);
        newExchange.setStatus(Exchange.ExchangeStatus.REQUESTED);
        return exchangeRepository.save(newExchange);
    }

    public Exchange updateExchangeStatus(int exchangeId, Exchange.ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found with ID: " + exchangeId));

        exchange.setStatus(status);
        
        // Set exchange date when status is ACCEPTED (book is actually borrowed/lent)
        if (status == Exchange.ExchangeStatus.ACCEPTED && exchange.getExchangeDate() == null) {
            exchange.setExchangeDate(LocalDate.now());
        }
        
        return exchangeRepository.save(exchange);
    }

    public List<Exchange> getExchangesByUser(int userId) {
        return exchangeRepository.findByBorrowerId(userId);
    }
    public List<Exchange> getExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBorrowerId(user.getId());
    }

    public List<Exchange> getLendingExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBookOwnerId(user.getId());
    }

}
