package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.BookDTO;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.mapper.BookMapper;
import com.bibliotek.personal.repository.BookRepository;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(int id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(BookMapper::toDTO).orElse(null);
    }

    public BookDTO createBook(BookDTO bookDTO, String email) {
        // Retrieve the user (owner)
        User owner = userRepository.findByEmail(email);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Convert DTO to entity
        Book book = BookMapper.toEntity(bookDTO, owner);
        Book savedBook = bookRepository.save(book);

        return BookMapper.toDTO(savedBook);
    }

    public BookDTO updateBook(int id, BookDTO bookDTO) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            return null;
        }
        Book book = existingBook.get();
        // Update book properties with DTO values
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setYear(bookDTO.getYear());
        book.setPublisher(bookDTO.getPublisher());
        book.setStatus(bookDTO.getStatus());
        bookRepository.save(book);
        return BookMapper.toDTO(book);
    }

    public boolean deleteBook(int id) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            bookRepository.delete(existingBook.get());
            return true;
        }
        return false;
    }

    public List<BookDTO> getBooksByUser(String email) {
        User user = userRepository.findByEmail(email);
        return bookRepository.findByOwner(user).stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }
}
