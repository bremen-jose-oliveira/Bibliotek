package com.Bibliotek.Personal.controller;


import com.Bibliotek.Personal.dao.BookDAO;
import com.Bibliotek.Personal.dao.BookDAOImpl;
import com.Bibliotek.Personal.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class testcontroller {
    @Autowired
    BookDAO book;

    //creating a  mapping for testing

    @GetMapping("/test")
    public  String test(Model theModel){

        theModel.addAttribute("theDate", new java.util.Date());

        return "/test";

    }

    @RequestMapping(value="/books", method=RequestMethod.GET)
    @ResponseBody
    public List<Book> getAllBooks() {
        return book.findAll();
    }





}
