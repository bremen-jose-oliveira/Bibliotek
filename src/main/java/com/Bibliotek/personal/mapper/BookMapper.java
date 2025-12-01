package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.BookDTO;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.BookDetails;
import com.bibliotek.personal.entity.User;

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
        bookDTO.setOwner(book.getOwner().getEmail());
        bookDTO.setOwnerUsername(book.getOwner().getUsername());

        // ✅ Ensure `readingStatus` is mapped correctly
        if (book.getReadingStatus() != null) {
            bookDTO.setReadingStatus(book.getReadingStatus().name()); // ✅ Convert Enum to String
        }

        // ✅ Ensure `exchangeStatus` is mapped correctly
        if (book.getExchangeStatus() != null) {
            bookDTO.setExchangeStatus(book.getExchangeStatus().name());
        }

        return bookDTO;
    }


    // Convert BookDTO to Book entity
    public static Book toEntity(User owner, BookDetails bookDetails) {
        return new Book(bookDetails, owner);
    }

}
