package com.Bibliotek.Personal;

import com.Bibliotek.Personal.dao.BookDAO;
import com.Bibliotek.Personal.dao.UserDAO;
import com.Bibliotek.Personal.entity.Book;
import com.Bibliotek.Personal.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BibliotekApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotekApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunner(BookDAO bookDAO){
		return runnner ->{


			createBook(bookDAO);
			//createUser(userDAO);
			//readUser(userDAO);
			//queryForUser(userDAO);
		};
	}


	private void createBook(BookDAO bookDAO){
		System.out.println("Add a new Book");

		Book testBook = new Book( "test2", "test2", 1923,"bla2331",1);


		System.out.println("saving the  new User");

		bookDAO.save(testBook);

		int bookId = testBook.getId();
		System.out.println("saved user. genarated id: " + bookId );

		Book testId = bookDAO.findById(bookId);
		System.out.println("found the Book: "+ testId);
	};



	private void queryForUser(UserDAO userDAO) {
		List<User> theUsers = userDAO.findAll();

		for(User tempUser : theUsers){
			System.out.println(tempUser);
		}
	}

	private void readUser(UserDAO userDAO) {
		System.out.println("create new User");
		User testUser = new User( "test", "test@test.com", "test123");

		System.out.println("saving the  new User");

		userDAO.save(testUser);
		System.out.println("saved user. genarated id: " +testUser.getId() );
	}

	private void createUser(UserDAO userDAO){
		System.out.println("create new User");
		User testUser = new User( "test", "test@test.com", "test123");


		System.out.println("saving the  new User");

		userDAO.save(testUser);

		int userId = testUser.getId();
		System.out.println("saved user. genarated id: " + userId );

		User testId = userDAO.findById(userId);
		System.out.println("found the user: "+ testId);
	};

}
