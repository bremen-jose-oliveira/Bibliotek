package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.UserBookStatusDTO;
import com.bibliotek.personal.entity.UserBookStatus;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.mapper.UserBookStatusMapper;
import com.bibliotek.personal.repository.UserBookStatusRepository;
import com.bibliotek.personal.repository.UserRepository;
import com.bibliotek.personal.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBookStatusService {

    private final UserBookStatusRepository userBookStatusRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public UserBookStatusService(UserBookStatusRepository userBookStatusRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.userBookStatusRepository = userBookStatusRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public UserBookStatusDTO updateBookStatus(int userId, int bookId, UserBookStatus.BookStatus status) {
        Optional<UserBookStatus> existingStatus = userBookStatusRepository.findByUserIdAndBookId(userId, bookId);

        UserBookStatus userBookStatus;
        if (existingStatus.isPresent()) {
            userBookStatus = existingStatus.get();
            userBookStatus.setStatus(status);
            userBookStatus = userBookStatusRepository.save(userBookStatus);
        } else {
            Optional<User> user = userRepository.findById(userId);
            Optional<Book> book = bookRepository.findById(bookId);

            if (user.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            if (book.isEmpty()) {
                throw new RuntimeException("Book not found with ID: " + bookId);
            }

            UserBookStatus newUserBookStatus = new UserBookStatus();
            newUserBookStatus.setUser(user.get());
            newUserBookStatus.setBook(book.get());
            newUserBookStatus.setStatus(status);
            try {
                userBookStatus = userBookStatusRepository.save(newUserBookStatus);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                throw new RuntimeException("Book not found or invalid. Please ensure the book exists in the database.", e);
            }
        }
        return UserBookStatusMapper.toDTO(userBookStatus);
    }

    public Optional<UserBookStatus.BookStatus> getBookStatus(int userId, int bookId) {
        return userBookStatusRepository.findByUserIdAndBookId(userId, bookId).map(UserBookStatus::getStatus);
    }

    public Optional<UserBookStatusDTO> getBookStatusDTO(int userId, int bookId) {
        return userBookStatusRepository.findByUserIdAndBookId(userId, bookId)
                .map(UserBookStatusMapper::toDTO);
    }

    public List<UserBookStatusDTO> getBookStatusesForLoggedInUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }
        return userBookStatusRepository.findByUserId(user.getId()).stream()
                .map(UserBookStatusMapper::toDTO)
                .collect(Collectors.toList());
    }
}