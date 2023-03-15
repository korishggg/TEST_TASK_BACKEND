package com.example.userservice.service.impl;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.IllegalOperationException;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.repo.UserRepository;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	private final static Logger LOGGER =  LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<UserDto> getAllUsers() {
		var users = userRepository.findAll();
		return UserDto.fromEntityList(users);
	}

	@Override
	public UserDto getUserById(Long id) {
		var user = getById(id);
		return UserDto.fromEntity(user);
	}

	@Override
	public UserDto createUser(CreateUserDto userDto) {
		var existingUser = userRepository.findByEmail(userDto.getEmail());
		if (existingUser.isPresent()) {
			LOGGER.info("User with this email = " + userDto.getEmail() + " already exists");
			throw new IllegalOperationException("This email is already taken");
		}
		var user = userDto.toEntity();
		user.setId(null);
		var createdUser = userRepository.save(user);
		LOGGER.info("User with this email = " + createdUser.getEmail() + " been created");
		return UserDto.fromEntity(createdUser);
	}

	@Override
	public UserDto updateUser(Long id, UpdateUserDto userDto) {
		var existingUser = getById(id);
		var hasChanged = false;
		if (!existingUser.getFirstName().equals(userDto.getFirstName())) {
			existingUser.setFirstName(userDto.getFirstName());
			hasChanged = true;
		}
		if (!existingUser.getLastName().equals(userDto.getLastName())) {
			existingUser.setLastName(userDto.getLastName());
			hasChanged = true;
		}
		if (!existingUser.getEmail().equals(userDto.getEmail())) {
			var emailToChange = userDto.getEmail();
			var optional =  userRepository.findByEmail(emailToChange);
			if (optional.isEmpty()) {
				existingUser.setEmail(userDto.getEmail());
				hasChanged = true;
			} else {
				throw new IllegalOperationException("This email is already taken");
			}
		}
		if (hasChanged) {
			var savedUser = userRepository.save(existingUser);
			return UserDto.fromEntity(savedUser);
		} else {
			LOGGER.info("User with this id = " + id + " haven`t any updates");
			return UserDto.fromEntity(existingUser);
		}
	}

	@Override
	public void deleteUser(Long id) {
		var existingUser = getById(id);
		userRepository.delete(existingUser);
		LOGGER.info("User with this email = " + existingUser.getEmail() + " been deleted");
	}

	private User getById(Long id) {
		return userRepository.findById(id)
							 .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
	}

}
