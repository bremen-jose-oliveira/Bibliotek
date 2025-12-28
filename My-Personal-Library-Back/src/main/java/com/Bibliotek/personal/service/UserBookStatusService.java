package com.Bibliotek.personal.service;


import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.entity.UserBookStatus;
import com.Bibliotek.personal.repository.BookRepository;
import com.Bibliotek.personal.repository.UserBookStatusRepository;
import com.Bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public UserBookStatus updateBookStatus(int userId, int bookId, UserBookStatus.BookStatus status) {
        Optional<UserBookStatus> existingStatus = userBookStatusRepository.findByUserIdAndBookId(userId, bookId);

        if (existingStatus.isPresent()) {
            UserBookStatus userBookStatus = existingStatus.get();
            userBookStatus.setStatus(status);
            return userBookStatusRepository.save(userBookStatus);
        } else {
            Optional<User> user = userRepository.findById(userId);
            Optional<Book> book = bookRepository.findById(bookId);

            if (user.isEmpty() || book.isEmpty()) {
                throw new RuntimeException("User or Book not found");
            }

            UserBookStatus newUserBookStatus = new UserBookStatus();
            newUserBookStatus.setUser(user.get());
            newUserBookStatus.setBook(book.get());
            newUserBookStatus.setStatus(status);
            return userBookStatusRepository.save(newUserBookStatus);
        }
    }

    public Optional<UserBookStatus.BookStatus> getBookStatus(int userId, int bookId) {
        return userBookStatusRepository.findByUserIdAndBookId(userId, bookId).map(UserBookStatus::getStatus);
    }
    
    public List<UserBookStatus> getAllStatusesForUser(int userId) {
        return userBookStatusRepository.findByUserId(userId);
    }
}