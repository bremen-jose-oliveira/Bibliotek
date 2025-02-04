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

    @PutMapping("/{id}") // Update an existing book
    public ResponseEntity<BookDTO> updateBook(@PathVariable Integer id, @RequestBody BookDTO bookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return (updatedBook != null) ? new ResponseEntity<>(updatedBook, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        List<BookDTO> userBooks = bookService.getBooksByUser(username);
        return new ResponseEntity<>(userBooks, HttpStatus.OK);
    }
}
