package com.example.userservice.service;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;

import java.util.List;

public interface UserService {

	List<UserDto> getAllUsers();

	UserDto getUserById(Long id);

	UserDto createUser(CreateUserDto userDto);

	UserDto updateUser(Long id, UpdateUserDto userDto);

	void deleteUser(Long id);
}
