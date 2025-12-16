package com.Bibliotek.personal.dto;



import java.time.LocalDateTime;
import java.util.List;

public class BookDTO {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String publisher;
    private String cover;
    private String owner;

    // New Fields for Statuses
    private String readingStatus;
    private String exchangeStatus;

    private List<ReviewDTO> reviews; // Use ReviewDTO here
    private List<ExchangeDTO> exchanges; // Use ExchangeDTO here
    private int reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public BookDTO() {
    }


    public BookDTO(int id, String isbn, String title, String author, int year, String publisher, String cover, String owner, String readingStatus, String exchangeStatus, int reviewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.cover = cover;
        this.owner = owner;
        this.readingStatus = readingStatus;
        this.exchangeStatus = exchangeStatus;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for full details (for `/books/{id}` endpoint)
    public BookDTO(int id, String isbn, String title, String author, int year, String publisher, String cover, String owner, String readingStatus, String exchangeStatus, List<ReviewDTO> reviews, List<ExchangeDTO> exchanges, int reviewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.cover = cover;
        this.owner = owner;
        this.readingStatus = readingStatus;
        this.exchangeStatus = exchangeStatus;
        this.reviews = reviews;
        this.exchanges = exchanges;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // Getters and Setters
    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public List<ExchangeDTO> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<ExchangeDTO> exchanges) {
        this.exchanges = exchanges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public String getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(String exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
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

    // Other getters and setters...
}


