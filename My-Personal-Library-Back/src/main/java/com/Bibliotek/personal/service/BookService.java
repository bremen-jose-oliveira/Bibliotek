package com.Bibliotek.personal.service;


import com.Bibliotek.personal.dto.BookDTO;
import com.Bibliotek.personal.dto.ExchangeDTO;
import com.Bibliotek.personal.dto.ReviewDTO;
import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.BookDetails;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.entity.UserBookStatus;
import com.Bibliotek.personal.mapper.BookMapper;
import com.Bibliotek.personal.mapper.ExchangeMapper;
import com.Bibliotek.personal.mapper.ReviewMapper;
import com.Bibliotek.personal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookDetailsRepository bookDetailsRepository;
    private final ReviewRepository reviewRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserBookStatusService userBookStatusService;

    private final UserBookStatusRepository userBookStatusRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UserRepository userRepository, BookDetailsRepository bookDetailsRepository, ReviewRepository reviewRepository, ExchangeRepository exchangeRepository, UserBookStatusService userBookStatusService, UserBookStatusRepository userBookStatusRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookDetailsRepository = bookDetailsRepository;
        this.reviewRepository = reviewRepository;
        this.exchangeRepository = exchangeRepository;
        this.userBookStatusService = userBookStatusService;
        this.userBookStatusRepository = userBookStatusRepository;
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

        // Check if BookDetails already exists by ISBN
        String isbn = bookDTO.getIsbn();
        BookDetails bookDetails = bookDetailsRepository.findByIsbn(isbn)
                .orElseGet(() -> {
                    // If not found, create new BookDetails
                    BookDetails newDetails = new BookDetails();
                    newDetails.setIsbn(bookDTO.getIsbn());
                    newDetails.setTitle(bookDTO.getTitle());
                    newDetails.setAuthor(bookDTO.getAuthor());
                    newDetails.setYear(bookDTO.getYear());
                    newDetails.setPublisher(bookDTO.getPublisher());
                    newDetails.setCover(bookDTO.getCover());
                    return bookDetailsRepository.save(newDetails);
                });

        // Convert DTO to entity
        Book book = BookMapper.toEntity(owner, bookDetails);
        Book savedBook = bookRepository.save(book);

        return BookMapper.toDTO(savedBook);
    }

    public BookDTO updateBook(int id, BookDTO bookDTO, String email) {
        User owner = userRepository.findByEmail(email);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        Optional<Book> existingBookOpt = bookRepository.findById(id);
        if (existingBookOpt.isEmpty()) {
            throw new RuntimeException("Book not found with ID: " + id);
        }

        Book book = existingBookOpt.get();

        // ✅ Ensure only the book owner can update it
        if (!Objects.equals(book.getOwner().getId(), owner.getId())) {
            throw new RuntimeException("Unauthorized: You are not the owner of this book.");
        }

        // ✅ Update `readingStatus` only if it's provided
        if (bookDTO.getReadingStatus() != null) {
            try {
                book.setReadingStatus(UserBookStatus.BookStatus.valueOf(bookDTO.getReadingStatus()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid readingStatus value: " + bookDTO.getReadingStatus());
            }
        }

        bookRepository.save(book); // ✅ Ensure update is saved

        return BookMapper.toDTO(book); // ✅ Ensure correct mapping
    }

    public boolean deleteBook(int id) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            bookRepository.delete(existingBook.get());
            return true;
        }
        return false;
    }

    public List<BookDTO> getBooksByOwner(String email) {
        User user = userRepository.findByEmail(email);
        return bookRepository.findByOwner(user).stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }



    public List<BookDTO> getBooksByOwnerWithDetails(String email) {
        User user = userRepository.findByEmail(email);
        List<Book> books = bookRepository.findByOwner(user);

        for (Book book : books) {
            book.setReviews(reviewRepository.findReviewsByBook(book));
            book.setExchanges(exchangeRepository.findExchangesByBook(book));
        }

        return books.stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BookDTO getBookWithExchanges(int bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (!bookOptional.isPresent()) {
            throw new RuntimeException("Book not found");
        }

        Book book = bookOptional.get();

        // Convert Book entity to BookDTO
        BookDTO bookDTO = BookMapper.toDTO(book);

        // Fetch and map exchanges for the book
        List<ExchangeDTO> exchangeDTOs = book.getExchanges().stream()
                .map(ExchangeMapper::toDTO)
                .collect(Collectors.toList());

        bookDTO.setExchanges(exchangeDTOs); // Set the exchanges in the BookDTO

        return bookDTO;
    }

    public BookDTO getBookWithReviews(int bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (!bookOptional.isPresent()) {
            throw new RuntimeException("Book not found");
        }

        Book book = bookOptional.get();

        // Convert Book entity to BookDTO
        BookDTO bookDTO = BookMapper.toDTO(book);

        // Fetch and map reviews for the book
        List<ReviewDTO> reviewDTOs = book.getReviews().stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());

        bookDTO.setReviews(reviewDTOs);  // Set the reviews in the BookDTO

        return bookDTO;
    }


    public List<BookDTO> getBooksByOwnerWithStatus(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return List.of();

        return bookRepository.findByOwner(user).stream().map(book -> {
            BookDTO bookDTO = BookMapper.toDTO(book); // Convert Book -> BookDTO
            Optional<UserBookStatus.BookStatus> status = userBookStatusService.getBookStatus(user.getId(), book.getId());
            status.ifPresent(s -> bookDTO.setReadingStatus(s.name())); // Set reading status
            return bookDTO;
        }).collect(Collectors.toList());
    }


    public BookDTO getFullBookDetails(int bookId) {
        Optional<Book> bookOptional = bookRepository.findByIdWithReviews(bookId);
        if (bookOptional.isEmpty()) return null;

        Book book = bookOptional.get();
        BookDTO bookDTO = BookMapper.toDTO(book);
        bookDTO.setOwner(book.getOwner().getEmail());

        // Fetch and attach reviews
        List<ReviewDTO> reviewDTOs = reviewRepository.findReviewsByBook(book)
                .stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());

        bookDTO.setReviews(reviewDTOs);
        bookDTO.setReviewCount(reviewDTOs.size());

        userBookStatusRepository.findStatusByBookId(bookId)
                .ifPresent(s -> bookDTO.setReadingStatus(s.name()));

        return bookDTO;
    }



}
