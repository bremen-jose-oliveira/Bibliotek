package com.Bibliotek.personal.mapper;

import com.Bibliotek.personal.dto.BookDTO;
import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.BookDetails;
import com.Bibliotek.personal.entity.User;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setIsbn(book.getBookDetails().getIsbn());
        bookDTO.setTitle(book.getBookDetails().getTitle());
        bookDTO.setAuthor(book.getBookDetails().getAuthor());
        bookDTO.setYear(book.getBookDetails().getYear());
        bookDTO.setPublisher(book.getBookDetails().getPublisher());
        bookDTO.setCover(book.getBookDetails().getCover());
        bookDTO.setDescription(book.getBookDetails().getDescription());
        bookDTO.setOwner(book.getOwner().getEmail());
        bookDTO.setOwnerUsername(book.getOwner().getUsername());

        if (book.getReadingStatus() != null) {
            bookDTO.setReadingStatus(book.getReadingStatus().name());
        }

        if (book.getExchangeStatus() != null) {
            bookDTO.setExchangeStatus(book.getExchangeStatus().name());
        }

        return bookDTO;
    }

    public static Book toEntity(User owner, BookDetails bookDetails) {
        return new Book(bookDetails, owner);
    }

}
