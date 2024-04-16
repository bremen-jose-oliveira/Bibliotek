package com.Bibliotek.Personal.entity;


import jakarta.persistence.*;


@Entity
@Table(name = "books")

public class Book {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int book_id;
    @Column(name = "Title")
    private String Title;
    @Column(name = "Author")
    private String Author;
    @Column(name = "Year")
    private int Year;
    @Column(name = "Publisher")
    private String Publisher;
    @Column(name = "user_id")
    private  int userId;



    public Book(String title, String author, int year, String publisher, int userId) {
        Title = title;
        Author = author;
        Year = year;
        Publisher = publisher;
        this.userId = userId;


    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
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
                "book_id=" + book_id +
                ", Title='" + Title + '\'' +
                ", Author='" + Author + '\'' +
                ", Year=" + Year +
                ", Publisher='" + Publisher + '\'' +
                ", userId=" + userId +
                '}';
    }
}
