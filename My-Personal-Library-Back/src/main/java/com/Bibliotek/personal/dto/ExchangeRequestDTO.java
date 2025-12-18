package com.Bibliotek.personal.dto;

public class ExchangeRequestDTO {
    private int borrowerId;
    private int bookId;

    public ExchangeRequestDTO() {
    }

    public ExchangeRequestDTO(int borrowerId, int bookId) {
        this.borrowerId = borrowerId;
        this.bookId = bookId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
