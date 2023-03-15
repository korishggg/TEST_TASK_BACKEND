package com.example.userservice;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceAppApplication implements CommandLineRunner {

	private UserService userService;

	public UserServiceAppApplication(UserService userService) {
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i = 0; i < 10; i++) {
			var createUser = new CreateUserDto("First Name-" + i, "Last Name-" + i, "test" + i + "@mail.com");
			userService.createUser(createUser);
		}
	}
}
