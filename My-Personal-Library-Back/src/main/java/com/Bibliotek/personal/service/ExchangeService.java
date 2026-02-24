package com.Bibliotek.personal.service;


import com.Bibliotek.personal.dto.ExchangeDTO;
import com.Bibliotek.personal.dto.ExchangeRequestDTO;
import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Exchange;
import com.Bibliotek.personal.entity.Notification;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.exception.InvalidRequestException;
import com.Bibliotek.personal.exception.ResourceNotFoundException;
import com.Bibliotek.personal.mapper.ExchangeMapper;
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
    private final NotificationService notificationService;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository, UserRepository userRepository,
                           BookRepository bookRepository, NotificationService notificationService) {
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }


    public ExchangeDTO requestExchange(ExchangeRequestDTO exchangeRequest) {
        User borrower = userRepository.findById(exchangeRequest.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + exchangeRequest.getBorrowerId()));

        Book book = bookRepository.findById(exchangeRequest.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + exchangeRequest.getBookId()));

        Exchange newExchange = new Exchange();
        newExchange.setBorrower(borrower);
        newExchange.setBook(book);
        newExchange.setStatus(Exchange.ExchangeStatus.REQUESTED);
        final Exchange saved = exchangeRepository.save(newExchange);

        User owner = book.getOwner();
        String bookTitle = book.getBookDetails() != null ? book.getBookDetails().getTitle() : "a book";
        if (owner != null) {
            notificationService.createNotification(
                    owner.getEmail(),
                    Notification.NotificationType.EXCHANGE_REQUEST,
                    "Borrow request",
                    borrower.getUsername() + " wants to borrow \"" + bookTitle + "\"",
                    borrower.getEmail(),
                    book.getId(),
                    saved.getId());
        }

        return exchangeRepository.findByIdWithBookAndBorrower(saved.getId())
                .map(ExchangeMapper::toDTO)
                .orElseGet(() -> ExchangeMapper.toDTO(saved));
    }

    public ExchangeDTO updateExchangeStatus(int exchangeId, Exchange.ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found with ID: " + exchangeId));

        exchange.setStatus(status);

        // Set exchange date when status is ACCEPTED (book is actually borrowed/lent)
        if (status == Exchange.ExchangeStatus.ACCEPTED && exchange.getExchangeDate() == null) {
            exchange.setExchangeDate(LocalDate.now());
        }

        final Exchange saved = exchangeRepository.save(exchange);

        User borrower = saved.getBorrower();
        Book book = saved.getBook();
        User owner = book != null ? book.getOwner() : null;
        String ownerUsername = owner != null ? owner.getUsername() : "The owner";
        String bookTitle = (book != null && book.getBookDetails() != null) ? book.getBookDetails().getTitle() : "the book";

        if (status == Exchange.ExchangeStatus.ACCEPTED && borrower != null) {
            notificationService.createNotification(
                    borrower.getEmail(),
                    Notification.NotificationType.EXCHANGE_ACCEPTED,
                    "Borrow request accepted",
                    ownerUsername + " accepted your request to borrow \"" + bookTitle + "\"",
                    owner != null ? owner.getEmail() : null,
                    book != null ? book.getId() : null,
                    saved.getId());
        } else if (status == Exchange.ExchangeStatus.REJECTED && borrower != null) {
            notificationService.createNotification(
                    borrower.getEmail(),
                    Notification.NotificationType.EXCHANGE_REJECTED,
                    "Borrow request declined",
                    ownerUsername + " declined your request to borrow \"" + bookTitle + "\"",
                    owner != null ? owner.getEmail() : null,
                    book != null ? book.getId() : null,
                    saved.getId());
        }

        return exchangeRepository.findByIdWithBookAndBorrower(exchangeId)
                .map(ExchangeMapper::toDTO)
                .orElseGet(() -> ExchangeMapper.toDTO(saved));
    }

    public List<Exchange> getExchangesByUser(int userId) {
        return exchangeRepository.findByBorrowerId(userId);
    }
    public List<ExchangeDTO> getExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBorrowerIdWithBookAndBorrower(user.getId()).stream()
                .map(ExchangeMapper::toDTO)
                .toList();
    }

    public List<ExchangeDTO> getLendingExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBookOwnerIdWithBookAndBorrower(user.getId()).stream()
                .map(ExchangeMapper::toDTO)
                .toList();
    }

    /**
     * Remove a RETURNED exchange from the list (allowed only for borrower or book owner).
     */
    public void deleteExchange(int exchangeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Exchange exchange = exchangeRepository.findByIdWithBookAndBorrower(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found with ID: " + exchangeId));

        if (exchange.getStatus() != Exchange.ExchangeStatus.RETURNED) {
            throw new InvalidRequestException("Only returned exchanges can be removed from the list.");
        }

        int borrowerId = exchange.getBorrower() != null ? exchange.getBorrower().getId() : -1;
        int ownerId = exchange.getBook() != null && exchange.getBook().getOwner() != null
                ? exchange.getBook().getOwner().getId() : -1;
        if (user.getId() != borrowerId && user.getId() != ownerId) {
            throw new InvalidRequestException("You can only remove your own returned borrows or lending records.");
        }

        exchangeRepository.delete(exchange);
    }

}
