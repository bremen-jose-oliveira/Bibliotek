package com.Bibliotek.Personal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "cover")
    private  String cover;

    @Column(name = "title")  // Use lowercase to match standard naming conventions
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "year")
    private int year;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "user_id")
    private int userId;



    // Default constructor
    public Book() {
    }

    // Constructor with parameters
    public Book(String cover,String title, String author, int year, String publisher, int userId) {
        this.cover = cover;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
        this.userId = userId;
    }

    // Getters and Setters
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", publisher='" + publisher + '\'' +
                ", userId=" + userId +
                '}';
    }
}
