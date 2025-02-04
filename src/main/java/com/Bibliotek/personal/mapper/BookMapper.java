package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.BookDTO;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.User;

public class BookMapper {

    // Convert Book entity to BookDTO
    public static BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setCover(book.getCover());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setYear(book.getYear());
        bookDTO.setPublisher(book.getPublisher());
        bookDTO.setOwner (book.getOwner().getEmail());
        bookDTO.setStatus(book.getStatus());
        bookDTO.setCreatedAt(book.getCreatedAt());
        bookDTO.setUpdatedAt(book.getUpdatedAt());
        return bookDTO;
    }

    // Convert BookDTO to Book entity
    public static Book toEntity(BookDTO bookDTO, User owner) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setCover(bookDTO.getCover());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setYear(bookDTO.getYear());
        book.setPublisher(bookDTO.getPublisher());
        book.setOwner(owner);  // Set the associated user as owner
        book.setStatus(bookDTO.getStatus());
        return book;
    }
}
