package com.Bibliotek.personal.dto;



import com.Bibliotek.personal.dto.user.UserDTO;
import com.Bibliotek.personal.entity.Exchange;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExchangeDTO {
    private int id;
    private BookDTO book; // BookDTO instead of Book
    private UserDTO borrower; // UserDTO instead of User
    private Exchange.ExchangeStatus status;
    private LocalDate exchangeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExchangeDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public UserDTO getBorrower() {
        return borrower;
    }

    public void setBorrower(UserDTO borrower) {
        this.borrower = borrower;
    }

    public Exchange.ExchangeStatus getStatus() {
        return status;
    }

    public void setStatus(Exchange.ExchangeStatus status) {
        this.status = status;
    }

    public LocalDate getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(LocalDate exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
