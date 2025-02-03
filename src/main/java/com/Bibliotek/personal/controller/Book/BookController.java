package com.bibliotek.personal.controller.Book;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.dao.Book.BookDAO;
import com.bibliotek.personal.dao.User.UserDAO;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/books")
public class BookController {

    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    @Autowired
    public BookController(BookDAO bookDAO, UserDAO userDAO) {
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookDAO.findById(id);
        return (book != null) ? new ResponseEntity<>(book, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping // Create a new book
    public ResponseEntity<Book> createBook(@RequestBody Book book) {

        // Get the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userDAO.findByUsername(currentUsername);
        book.setUser(currentUser);



        bookDAO.save(book);

        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}") // Update an existing book
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        Book existingBook = bookDAO.findById(id);
        if (existingBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        book.setId(id);
        bookDAO.save(book);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Integer id) {
        Book existingBook = bookDAO.findById(id);

        if (existingBook == null) {
            return new ResponseEntity<>(new ApiResponse("Book not found"), HttpStatus.NOT_FOUND);
        }

        bookDAO.delete(existingBook);

        // Return a JSON response with a message indicating successful deletion
        return new ResponseEntity<>(new ApiResponse("Book deleted successfully"), HttpStatus.OK);
    }


    @GetMapping("/my")
    public ResponseEntity<List<Book>> getBooksForCurrentUser() {

        // Retrieve the current logged-in user’s username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        User currentUser = userDAO.findByUsername(username);
        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Find books associated with this user
        List<Book> userBooks = bookDAO.findByUser(currentUser);

        return ResponseEntity.ok(userBooks);
    }

}