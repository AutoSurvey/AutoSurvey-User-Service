package com.revature.autosurvey.users.services.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.users.beans.User;
import com.revature.autosurvey.users.data.UserRepository;
import com.revature.autosurvey.users.services.UserService;
import com.revature.autosurvey.users.services.UserServiceImp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

	@TestConfiguration
	static class Config {

		@Bean
		public UserService getUserService(UserRepository userRepository) {
			UserServiceImp usi = new UserServiceImp();
			usi.setUserRepo(userRepository);
			return usi;
		}

		@Bean
		public UserRepository getUserRepo() {
			return Mockito.mock(UserRepository.class);
		}
	}

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Test
	void userServiceReturnsNull() {
		User user = new User();

//		assertThat(userService.addUser(user)).isNull();
//		assertThat(userService.deleteUser("test")).isNull();
//		assertThat(userService.getAllUsers()).isNull();
//		assertThat(userService.getUserById("test")).isNull();
//		assertThat(userService.getUserByEmail("test")).isNull();
		assertThat(userService.updateUser(user)).isNull();
	}

	@Test
	void getAllUsersreturnsFlux() {
		User u = new User();
		u.setEmail("a");
		u.setPassword("c");
		User u2 = new User();
		u2.setEmail("b");
		u2.setPassword("d");
		User[] uArr = { u, u2 };
		Flux<User> uFlux = Flux.fromArray(uArr);

		Mockito.when(userRepository.findAll()).thenReturn(uFlux);
		Flux<User> resultFlux = userService.getAllUsers();

		StepVerifier.create(resultFlux).expectNext(u).expectNext(u2).verifyComplete();
	}

	@Test
	void deleteUserReturnsDeletedUser() {
		User u = new User();
		u.setEmail("a@a.com");
		u.setUsername("a");
		String email = u.getEmail();
		
		when(userRepository.existsById(email)).thenReturn(Mono.just(true));
		when(userRepository.findById(email)).thenReturn(Mono.just(u));
		when(userRepository.deleteById(email)).thenReturn(Mono.empty());
		Mono<User> result = userService.deleteUser("a@a.com");
		StepVerifier.create(result)
		.expectNext(u)
		.expectComplete()
		.verify();
	}

	@Test
	void deleteUserReturnsEmptyIfnoUser() {
//		User user = new User();
		Mono<User> noOne = Mono.empty();
		when(userRepository.existsById("a")).thenReturn(Mono.just(false));
		Mono<User> result = userService.deleteUser("a");	
		Mono<Boolean> comparer = Mono.sequenceEqual(result, noOne);
		StepVerifier.create(comparer).expectNext(true).verifyComplete();
	}

	@Test
	void testAddUserAddsUser() {
		User user = new User();
		user.setEmail("test@test.com");
		Mockito.when(userRepository.existsById(user.getEmail())).thenReturn(Mono.just(Boolean.FALSE));
		Mockito.when(userRepository.insert(user)).thenReturn(Mono.just(user));
		Mono<User> result = userService.addUser(user);
		StepVerifier.create(result).expectNext(user).verifyComplete();
	}

	@Test
	void testAddUserFailReturnEmpty() {
		User user = new User();
		Mockito.when(userRepository.existsById(user.getEmail())).thenReturn(Mono.just(Boolean.TRUE));
		Mockito.when(userRepository.insert(user)).thenReturn(Mono.empty());
		Mono<User> result = userService.addUser(user);
		StepVerifier.create(result).verifyComplete();
	}

	@Test
	void testUpdateUser() {
		User u1 = new User();
		u1.setEmail("text@text.com");
		u1.setPassword("false");
		Mockito.when(userRepository.existsById("text@text.com")).thenReturn(Mono.just(Boolean.TRUE));
		Mockito.when(userRepository.save(u1)).thenReturn(Mono.just(u1));
		u1.setPassword("true");
		Mono<User> result = userService.updateUser(u1);
		StepVerifier.create(result).expectNextMatches(u -> u.getPassword().equals("true")).verifyComplete();
	}

	@Test
	void testGetUserByEmailGetsUserByEmail() {
		User user = new User();
		user.setEmail("test@test.com");
		Mockito.when(userRepository.existsById("test@test.com")).thenReturn(Mono.just(Boolean.TRUE));
		Mockito.when(userRepository.findById(user.getEmail())).thenReturn(Mono.just(user));
		Mono<User> result = userService.getUserByEmail("test@test.com");
		StepVerifier.create(result).expectNext(user).verifyComplete();
	}

	@Test
	void testGetUserByEmailFailReturnsEmpty() {
		Mockito.when(userRepository.existsById("test@test.com")).thenReturn(Mono.just(Boolean.FALSE));
		Mockito.when(userRepository.findById("test@test.com")).thenReturn(Mono.empty());
		Mono<User> result = userService.getUserByEmail("test@test.com");
		StepVerifier.create(result).verifyComplete();
	}

	@Test
	void testGetUserByIdGetsUserById() {
		User user = new User();
		user.setEmail("test@test.com");
		Mockito.when(userRepository.existsById("test@test.com")).thenReturn(Mono.just(Boolean.TRUE));
		Mockito.when(userRepository.findById(user.getEmail())).thenReturn(Mono.just(user));
		Mono<User> result = userService.getUserById("test@test.com");
		StepVerifier.create(result).expectNext(user).verifyComplete();
	}

	@Test
	void testGetUserByIdFailReturnsEmpty() {
		Mockito.when(userRepository.existsById("test@test.com")).thenReturn(Mono.just(Boolean.FALSE));
		Mockito.when(userRepository.findById("test@test.com")).thenReturn(Mono.empty());
		Mono<User> result = userService.getUserById("test@test.com");
		StepVerifier.create(result).verifyComplete();
	}
}
