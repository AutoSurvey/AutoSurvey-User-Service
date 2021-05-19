package com.revature.autosurvey.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.revature.autosurvey.users.beans.User;
import com.revature.autosurvey.users.services.UserService;

import reactor.core.publisher.Mono;

@Component
//@RestController
//@RequestMapping(value = "/users")
public class UserController {
	@Autowired
	private UserService userService;
	private User emptyUser;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public Mono<ServerResponse> getUsers(ServerRequest req) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(userService.getAllUsers(), User.class);
	}

//	@PostMapping
	public Mono<ResponseEntity<User>> addUser(@RequestBody User user) {
		return userService.addUser(user).defaultIfEmpty(emptyUser).map(u -> {
			if (u.getUsername() == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(u);
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(u);
		});
	}

//	@PutMapping
	public void login() {

	}

//	@GetMapping("/{id}")
	public void getUserById() {

	}

//	@GetMapping
	public void getUserEmail() {

	}

//	@PutMapping("/{id}")
	public void updateUser() {

	}

//	@DeleteMapping("/{id}")
	public void deleteUser() {

	}

}
