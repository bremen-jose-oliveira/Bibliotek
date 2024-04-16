package com.Bibliotek.Personal.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class testcontroller {

    //creating a  mapping for testing

    @GetMapping("/test")
    public  String test(Model theModel){

        theModel.addAttribute("theDate", new java.util.Date());

        return "/test";

    }

}
