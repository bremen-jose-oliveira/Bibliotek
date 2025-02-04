package com.bibliotek.personal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "cover")
    private String cover;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "year")
    private int year;

    @Column(name = "publisher")
    private String publisher;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Exchange> exchanges = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status = BookStatus.NOT_READ;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BookStatus {
        NOT_READ,
        READING,
        READ
    }
    public Book() {
    }

    public Book(String cover, String title, String author, int year, String publisher, User owner, BookStatus status) {
        this.cover = cover;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.owner = owner;
        this.status = status != null ? status : BookStatus.NOT_READ;
    }

    public Book(String cover, String title, String author, int year, String publisher, User owner, BookStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cover = cover;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.owner = owner;
        this.status = status != null ? status : BookStatus.NOT_READ;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Book(String cover, String title, String author, int year, String publisher, User owner, List<Review> reviews, List<Exchange> exchanges, BookStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cover = cover;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.owner = owner;
        this.reviews = reviews;
        this.exchanges = exchanges;
        this.status = status != null ? status : BookStatus.NOT_READ;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void ensureStatus() {
        if (this.status == null) {
            this.status = BookStatus.NOT_READ;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
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
