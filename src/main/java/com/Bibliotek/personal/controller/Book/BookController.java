package com.bibliotek.personal.controller.Book;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.dto.BookDTO;
import com.bibliotek.personal.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Integer id) {
        BookDTO book = bookService.getBookById(id);
        return (book != null) ? new ResponseEntity<>(book, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @PostMapping // Create a new book
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        BookDTO createdBook = bookService.createBook(bookDTO, currentUsername);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Integer id, @RequestBody BookDTO bookDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        BookDTO updatedBook = bookService.updateBook(id, bookDTO, currentUsername);

        if (updatedBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Integer id) {
        boolean success = bookService.deleteBook(id);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Book deleted successfully", true, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Book not found", false, 404), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookDTO>> getBooksForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<BookDTO> userBooks = bookService.getBooksByOwner(username);
        return new ResponseEntity<>(userBooks, HttpStatus.OK);
    }
    // New Route 1: Get books of the owner with reading status
    @GetMapping("/my/with-status")
    public ResponseEntity<List<BookDTO>> getBooksForCurrentUserWithStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<BookDTO> userBooks = bookService.getBooksByOwnerWithStatus(username);
        return new ResponseEntity<>(userBooks, HttpStatus.OK);
    }

    // New Route 2: Get full details of a book by ID
    @GetMapping("/details/{id}")
    public ResponseEntity<BookDTO> getBookDetails(@PathVariable Integer id) {
        BookDTO bookDetails = bookService.getFullBookDetails(id);
        return (bookDetails != null) ? new ResponseEntity<>(bookDetails, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get books by user email (for viewing friend's books)
    @GetMapping("/user/{email}")
    public ResponseEntity<List<BookDTO>> getBooksByUserEmail(@PathVariable String email) {
        List<BookDTO> books = bookService.getBooksByOwner(email);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


}
