package com.Bibliotek.Personal;

import com.Bibliotek.Personal.dao.BookDAO;
import com.Bibliotek.Personal.dao.UserDAO;
import com.Bibliotek.Personal.entity.Book;
import com.Bibliotek.Personal.entity.User;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.cache.NonUniqueCacheException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class BibliotekApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotekApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunner(UserDAO userDAO, BookDAO bookDAO){


		return runnner ->{

		Stream.of("John", "Julie", "Jennifer", "Helen", "Rachel").forEach(username -> {

				String email = generateRandomEmail(username);
				String password = generateRandomPassword();
				User user = new User(username, email, password);
				userDAO.save(user);
				System.out.println("Saved user: " + user);


			});


			userDAO.findAll().forEach(System.out::println);
			Stream.of(
					new Book(null,"The Good Daughter", "Karin Slaughter", 2017, "William Morrow", 1),
					new Book(null,"Pretty Girls", "Karin Slaughter", 2015, "William Morrow", 1),
					new Book(null,"Cop Town", "Karin Slaughter", 2014, "Delacorte Press", 1),
					new Book(null,"Blindsighted", "Karin Slaughter", 2001, "William Morrow", 1),
					new Book(null,"The Shining", "Stephen King", 1977, "Doubleday", 1),
					new Book(null,"It", "Stephen King", 1986, "Viking Press", 1),
					new Book(null,"Misery", "Stephen King", 1987, "Viking Press", 1),
					new Book(null,"The Stand", "Stephen King", 1978, "Doubleday", 1),
					new Book(null,"The Other Child", "Charlotte Link", 2008, "Orion Publishing Group", 1),
					new Book(null,"The Watcher", "Charlotte Link", 2007, "Orion Publishing Group", 1)
			).forEach(book -> bookDAO.save(book));



			bookDAO.findAll().forEach(System.out::println);


			//createUser(userDAO);
			//createBook(bookDAO);

			//readUser(userDAO);
			//queryForUser(userDAO);
		};
	}

	private String generateRandomEmail(String username) {
		return username.toLowerCase() + "@" + UUID.randomUUID().toString() + ".com";
	}


	private String generateRandomPassword() {

		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		StringBuilder password = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			password.append(chars.charAt(random.nextInt(chars.length())));
		}
		return password.toString();
	}
	private void createBook(BookDAO bookDAO){
		System.out.println("Add a new Book");

		Book testBook = new Book(null, "test2", "test2", 1923,"bla2331",1);


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

