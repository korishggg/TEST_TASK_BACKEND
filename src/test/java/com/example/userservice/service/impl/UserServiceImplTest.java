package com.example.userservice.service.impl;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UpdateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.IllegalOperationException;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepository);
	}

	@Test
	void testGetAllUsers() {
		var user1 = new User("John", "Doe", "john.doe@example.com");
		var user2 = new User("Jane", "Doe", "jane.doe@example.com");
		var users = Arrays.asList(user1, user2);

		when(userRepository.findAll()).thenReturn(users);

		var userDtos = userService.getAllUsers();

		assertNotNull(userDtos);
		assertEquals(2, userDtos.size());

		assertEquals("John", userDtos.get(0).getFirstName());
		assertEquals("Doe", userDtos.get(0).getLastName());
		assertEquals("john.doe@example.com", userDtos.get(0).getEmail());

		assertEquals("Jane", userDtos.get(1).getFirstName());
		assertEquals("Doe", userDtos.get(1).getLastName());
		assertEquals("jane.doe@example.com", userDtos.get(1).getEmail());
	}

	@Test
	void testGetUserById() {
		var user = new User("John", "Doe", "john.doe@example.com");

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		var userDto = userService.getUserById(user.getId());

		assertNotNull(userDto);
		assertEquals("John", userDto.getFirstName());
		assertEquals("Doe", userDto.getLastName());
		assertEquals("john.doe@example.com", userDto.getEmail());
	}

	@Test
	void testGetUserByIdNotFound() {
		var id = 1L;

		when(userRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));
	}

	@Test
	void testCreateUser() {
		var email = "john.doe@example.com";
		var createUserDto = new CreateUserDto("John", "Doe", email);
		var user = new User(createUserDto.getFirstName(), createUserDto.getLastName(), email);

		when(userRepository.save(any(User.class))).thenReturn(user);

		var userDto = userService.createUser(createUserDto);

		assertNotNull(userDto);
		assertEquals("John", userDto.getFirstName());
		assertEquals("Doe", userDto.getLastName());
		assertEquals("john.doe@example.com", userDto.getEmail());


		verify(userRepository).findByEmail(email);
		verify(userRepository).save(user);
	}

	@Test
	void testCreateUserWithSameEmailAlreadyExists() {
		var email = "john.doe@example.com";
		var createUserDto = new CreateUserDto("John", "Doe", email);
		var user = new User(createUserDto.getFirstName(), createUserDto.getLastName(), email);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		assertThrows(IllegalOperationException.class, () -> userService.createUser(createUserDto));
	}

	@Test
	void testUpdateUserWhenUserIsNotFoundById() {
		var userId = 1L;
		var updatedUser = new UpdateUserDto("Test", "Doe", "johndoe@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updatedUser));
	}

	@Test
	void testUpdateUserWithOutChanges() {
		var userId = 1L;

		var existingUser = new User("John", "Doe", "johndoe@example.com");
		var updatedUser = new UpdateUserDto("John", "Doe", "johndoe@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		userService.updateUser(userId, updatedUser);

		verify(userRepository).findById(userId);
		verify(userRepository, times(0)).save(any(User.class));
	}

	@ParameterizedTest(name = "Test case {index}: updatedUser={0}, expectedUser={1}")
	@MethodSource("updateUserTestData")
	void testUpdateUserWithChanges(UpdateUserDto updatedUser, User existingUser, User expectedUser) {
		var userId = existingUser.getId();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(existingUser)).thenReturn(expectedUser);

		var result = userService.updateUser(userId, updatedUser);

		assertThat(result).isEqualTo(UserDto.fromEntity(expectedUser));
		verify(userRepository).findById(userId);
		verify(userRepository).save(any(User.class));
	}

	@Test
	void testUpdateUserWithEmailChangeAlreadyExist() {
		var emailToChange = "test@example.com";
		var existingUser = new User("John", "Doe", "johndoe@example.com");
		var updatedUser = new UpdateUserDto("John", "Doe", emailToChange);
		var userId = existingUser.getId();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmail(emailToChange)).thenReturn(Optional.of(mock(User.class)));

		assertThrows(IllegalOperationException.class, () -> userService.updateUser(userId, updatedUser));
	}

	@Test
	void testUpdateUserWithEmailChange() {
		var emailToChange = "test@example.com";
		var existingUser = new User("John", "Doe", "johndoe@example.com");
		var updatedUser = new UpdateUserDto("John", "Doe", emailToChange);
		var expectedUser = new User("John", "Doe", emailToChange);
		var userId = existingUser.getId();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmail(emailToChange)).thenReturn(Optional.empty());
		when(userRepository.save(existingUser)).thenReturn(expectedUser);

		var result = userService.updateUser(userId, updatedUser);

		assertEquals(result.getEmail(), emailToChange);
		verify(userRepository).findById(userId);
		verify(userRepository).findByEmail(emailToChange);
		verify(userRepository).save(any(User.class));
	}

	@Test
	void testDeleteUserWhenUserIsNotFound() {
		var userId = 1L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
	}

	@Test
	void testDeleteUser() {
		var userId = 1L;
		var existingUser = new User("John", "Doe", "johndoe@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

		userService.deleteUser(userId);

		verify(userRepository).findById(userId);
		verify(userRepository).delete(any(User.class));

	}

	static Stream<Arguments> updateUserTestData() {
		User existingUser = new User("John", "Doe", "johndoe@example.com");

		UpdateUserDto updatedFirstName = new UpdateUserDto("Test", "Doe", "johndoe@example.com");
		User expectedUserAfterFirstNameUpdate = new User("Test", "Doe", "johndoe@example.com");

		UpdateUserDto updatedLastName = new UpdateUserDto("John", "Test", "johndoe@example.com");
		User expectedUserAfterLastNameUpdate = new User("John", "Test", "johndoe@example.com");

		return Stream.of(
				Arguments.of(updatedFirstName, existingUser, expectedUserAfterFirstNameUpdate),
				Arguments.of(updatedLastName, existingUser, expectedUserAfterLastNameUpdate)
		);
	}

}
