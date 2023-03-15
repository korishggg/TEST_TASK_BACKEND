package com.example.userservice.api;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public List<UserDto> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public UserDto getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PostMapping
	public UserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
		return userService.createUser(createUserDto);
	}

	@PatchMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto userDto) {
		return userService.updateUser(id, userDto);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}
