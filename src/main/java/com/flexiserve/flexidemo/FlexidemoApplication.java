package com.flexiserve.flexidemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FlexidemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexidemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunner(String[] args){
		return runnner ->{
			System.out.println("Hello World");
		};
	}

}
