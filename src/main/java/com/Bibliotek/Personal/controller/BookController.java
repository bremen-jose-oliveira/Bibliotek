package com.Bibliotek.Personal.controller;

import com.Bibliotek.Personal.dao.BookDAO;
import com.Bibliotek.Personal.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookDAO bookDAO;

    @Autowired
    public BookController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @GetMapping // Get all books
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    @GetMapping("/{id}") // Get a book by ID
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        Book book = bookDAO.findById(id);
        return (book != null) ? new ResponseEntity<>(book, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping // Create a new book
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        bookDAO.save(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @PutMapping("/{id}") // Update an existing book
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        Book existingBook = bookDAO.findById(id);
        if (existingBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        book.setId(id); // Ensure the ID is set for the update
        bookDAO.save(book);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Delete a book
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        Book existingBook = bookDAO.findById(id);
        if (existingBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bookDAO.delete(existingBook); // Add a delete method in BookDAO
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
