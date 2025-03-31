package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.ExchangeRequestDTO;
import com.bibliotek.personal.entity.Exchange;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.exception.ResourceNotFoundException;
import com.bibliotek.personal.repository.ExchangeRepository;
import com.bibliotek.personal.repository.UserRepository;
import com.bibliotek.personal.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
        return exchangeRepository.save(exchange);
    }

    public List<Exchange> getExchangesByUser(int userId) {
        return exchangeRepository.findByBorrowerId(userId);
    }
    public List<Exchange> getExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBorrowerId(user.getId());
    }

}
