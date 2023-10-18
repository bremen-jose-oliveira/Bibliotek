package com.flexiserve.flexidemo;

import com.flexiserve.flexidemo.dao.UserDAO;
import com.flexiserve.flexidemo.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class FlexidemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexidemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunner(UserDAO userDAO){
		return runnner ->{
			//createUser(userDAO);
			//readUser(userDAO);
			queryForUser(userDAO);
		};
	}

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
