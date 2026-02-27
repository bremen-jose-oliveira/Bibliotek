package com.Bibliotek.personal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_details", uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
public class BookDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String publisher;

    // Cover URLs from Google/Open Library can be longer than 255 chars, so store as TEXT.
    @Column(columnDefinition = "TEXT")
    private String cover;

    // Descriptions can be very long (full summaries), so store as TEXT.
    @Column(columnDefinition = "TEXT")
    private String description;

    public BookDetails() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
