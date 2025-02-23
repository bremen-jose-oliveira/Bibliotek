package com.bibliotek.personal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "book_details_id", nullable = false)
    private BookDetails bookDetails;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Exchange> exchanges;

    // ✅ Added Reading Status (Enum)
    @Enumerated(EnumType.STRING)
    private UserBookStatus.BookStatus readingStatus;

    // ✅ Added Exchange Status (Enum)
    @Enumerated(EnumType.STRING)
    private Exchange.ExchangeStatus exchangeStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Book() {}
    public boolean isOwnedBy(User user) {
        return Objects.equals(this.owner.getId(), user.getId());
    }

    public Book(BookDetails bookDetails, User owner) {
        this.bookDetails = bookDetails;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BookDetails getBookDetails() {
        return bookDetails;
    }

    public void setBookDetails(BookDetails bookDetails) {
        this.bookDetails = bookDetails;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<Exchange> exchanges) {
        this.exchanges = exchanges;
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

    // ✅ Getters & Setters for Reading and Exchange Status
    public UserBookStatus.BookStatus getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(UserBookStatus.BookStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public Exchange.ExchangeStatus getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(Exchange.ExchangeStatus exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }
}
