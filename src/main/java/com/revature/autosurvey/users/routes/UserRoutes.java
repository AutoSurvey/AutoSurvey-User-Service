package com.revature.autosurvey.users.routes;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import com.revature.autosurvey.users.errors.NotFoundException;
import com.revature.autosurvey.users.errors.UserAlreadyExistsException;
import com.revature.autosurvey.users.handlers.UserHandler;



@Configuration
public class UserRoutes {
	@Autowired
	private UserHandler uh;
	
	@Bean
	RouterFunction<ServerResponse> routes() {
		return RouterFunctions.route().path("/",
				builder -> builder
				.GET("id", RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::getIdTable)
				.GET("{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::getUserById)
				.DELETE("{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::deleteUser)
				.PUT("{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::updateUser)
				.GET(RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::getUsers)
				.POST(RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::addUser)
				.PUT(RequestPredicates.accept(MediaType.APPLICATION_JSON), uh::login))
				.build();
	}
	
	@Bean
	WebFilter exceptionToErrorCode() {
		return (exchange, next) -> next.filter(exchange)
				.onErrorResume(NotFoundException.class, e -> {
					ServerHttpResponse response = exchange.getResponse();
					response.setRawStatusCode(HttpStatus.SC_NOT_FOUND);
					return response.setComplete();
				})
				.onErrorResume(UserAlreadyExistsException.class, e -> {
					ServerHttpResponse response = exchange.getResponse();
					response.setRawStatusCode(HttpStatus.SC_CONFLICT);
					return response.setComplete();
				});
	}
}
