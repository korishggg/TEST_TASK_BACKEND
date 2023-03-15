package com.example.userservice.dto;

import com.example.userservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

	@NotBlank(message = "First name is mandatory")
	private String firstName;

	@NotBlank(message = "Last name is mandatory")
	private String lastName;

	@Email(message = "Email should be valid")
	@NotBlank(message = "Email is mandatory")
	private String email;

	public User toEntity() {
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		return user;
	}
}
