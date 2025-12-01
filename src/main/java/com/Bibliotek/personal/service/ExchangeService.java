package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.ExchangeDTO;
import com.bibliotek.personal.dto.ExchangeRequestDTO;
import com.bibliotek.personal.entity.Exchange;
import com.bibliotek.personal.entity.Notification;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.exception.ResourceNotFoundException;
import com.bibliotek.personal.mapper.ExchangeMapper;
import com.bibliotek.personal.repository.ExchangeRepository;
import com.bibliotek.personal.repository.UserRepository;
import com.bibliotek.personal.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationService notificationService;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository, 
                          UserRepository userRepository, 
                          BookRepository bookRepository,
                          NotificationService notificationService) {
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
        Exchange savedExchange = exchangeRepository.save(newExchange);
        
        // Create notification for book owner
        if (book.getOwner() != null && !book.getOwner().getEmail().equals(borrower.getEmail())) {
            notificationService.createNotification(
                book.getOwner().getEmail(),
                Notification.NotificationType.EXCHANGE_REQUEST,
                "New Exchange Request",
                borrower.getUsername() + " wants to borrow \"" + book.getBookDetails().getTitle() + "\"",
                borrower.getEmail(),
                book.getId(),
                savedExchange.getId()
            );
        }
        
        return ExchangeMapper.toDTO(savedExchange);
    }

    public ExchangeDTO updateExchangeStatus(int exchangeId, Exchange.ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found with ID: " + exchangeId));

        Exchange.ExchangeStatus oldStatus = exchange.getStatus();
        exchange.setStatus(status);
        Exchange updatedExchange = exchangeRepository.save(exchange);
        
        // Create notifications based on status change
        if (oldStatus != status) {
            String borrowerEmail = exchange.getBorrower().getEmail();
            String ownerEmail = exchange.getBook().getOwner().getEmail();
            String bookTitle = exchange.getBook().getBookDetails().getTitle();
            String ownerUsername = exchange.getBook().getOwner().getUsername();
            String borrowerUsername = exchange.getBorrower().getUsername();
            
            switch (status) {
                case ACCEPTED:
                    // Notify borrower
                    notificationService.createNotification(
                        borrowerEmail,
                        Notification.NotificationType.EXCHANGE_ACCEPTED,
                        "Exchange Accepted",
                        "Your request to borrow \"" + bookTitle + "\" was accepted by " + ownerUsername,
                        ownerEmail,
                        exchange.getBook().getId(),
                        exchangeId
                    );
                    break;
                case REJECTED:
                    // Notify borrower
                    notificationService.createNotification(
                        borrowerEmail,
                        Notification.NotificationType.EXCHANGE_REJECTED,
                        "Exchange Rejected",
                        "Your request to borrow \"" + bookTitle + "\" was rejected",
                        ownerEmail,
                        exchange.getBook().getId(),
                        exchangeId
                    );
                    break;
                case RETURNED:
                    // Notify owner
                    notificationService.createNotification(
                        ownerEmail,
                        Notification.NotificationType.EXCHANGE_RETURNED,
                        "Book Returned",
                        "\"" + bookTitle + "\" has been returned by " + borrowerUsername,
                        borrowerEmail,
                        exchange.getBook().getId(),
                        exchangeId
                    );
                    break;
                case REQUESTED:
                    // No notification needed for REQUESTED as it's handled in requestExchange
                    break;
            }
        }
        
        return ExchangeMapper.toDTO(updatedExchange);
    }

    public List<ExchangeDTO> getExchangesByUser(int userId) {
        return exchangeRepository.findByBorrowerId(userId).stream()
                .map(ExchangeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<ExchangeDTO> getExchangesForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return exchangeRepository.findByBorrowerId(user.getId()).stream()
                .map(ExchangeMapper::toDTO)
                .collect(Collectors.toList());
    }

}
