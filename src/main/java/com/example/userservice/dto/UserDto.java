package com.example.userservice.dto;

import com.example.userservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;

	@NotBlank(message = "First name is mandatory")
	private String firstName;

	@NotBlank(message = "Last name is mandatory")
	private String lastName;

	@Email(message = "Email should be valid")
	@NotBlank(message = "Email is mandatory")
	private String email;

	public static UserDto fromEntity(User user) {
		return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
	}

	public static List<UserDto> fromEntityList(List<User> users) {
		return users.stream()
				.map(UserDto::fromEntity)
				.collect(Collectors.toList());
	}
}
