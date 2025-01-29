package com.Bibliotek.Personal;

import com.Bibliotek.Personal.dao.Book.BookDAO;
import com.Bibliotek.Personal.dao.User.UserDAO;
import com.Bibliotek.Personal.entity.Book;
import com.Bibliotek.Personal.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class BibliotekApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotekApplication.class, args);
	}


}

