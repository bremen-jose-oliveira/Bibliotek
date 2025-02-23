package com.bibliotek.personal.dto;

import com.bibliotek.personal.dto.user.UserDTO;

public class UserBookStatusDTO {
    private int id;
    private BookDTO book;
    private UserDTO user;
    private String status;  // BookStatus enum or string
    private String visibility;  // BookVisibility enum or string

    public UserBookStatusDTO() {
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}