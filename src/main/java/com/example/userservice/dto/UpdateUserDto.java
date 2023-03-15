package com.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

	@NotBlank(message = "First name is mandatory")
	private String firstName;

	@NotBlank(message = "Last name is mandatory")
	private String lastName;

	@Email(message = "Email should be valid")
	@NotBlank(message = "Email is mandatory")
	private String email;
}
