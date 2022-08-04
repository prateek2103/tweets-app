package com.tweetapp.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.model.UserToken;
import com.tweetapp.service.IUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

	@Autowired
	private IUserService userService;

	@Autowired
	JwtUtil jwtUtil;

	/**
	 * authenticates the user
	 * 
	 * @param userModel
	 * @return userToken
	 * @throws InvalidUserException
	 */
	@PostMapping("/login")
	public ResponseEntity<UserToken> login(@RequestBody UserDoc user) throws InvalidUserException {

		// validate the user and get the token
		UserToken token = userService.loginUser(user);

		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	/**
	 * register a new user to the tweets application
	 * 
	 * @param userDoc
	 * @return
	 * @throws InvalidUserException
	 */
	@PostMapping("/register")
	public ResponseEntity<String> registerNewUser(@RequestBody UserDoc user) throws InvalidUserException {

		log.info("signing up new user with username:{}", user.getUsername());

		// register the user
		userService.registerUser(user);

		log.info("user signed up successfully");

		return new ResponseEntity<>(TweetConstants.TWEETS_USER_CREATED_MESSAGE, HttpStatus.CREATED);
	}

	/**
	 * method to create new password for logged in user
	 * 
	 * @param token
	 * @param password
	 * @return
	 * @throws InvalidTokenException
	 * @throws InvalidUserException
	 */
	@PostMapping("/{username}/forgetPassword")
	public ResponseEntity<String> updateForgottonPassword(@PathVariable("username") String username,
			@RequestHeader("Authorization") String token, @RequestBody String password)
			throws InvalidTokenException, InvalidUserException {

		// if password is empty
		if (StringUtils.isEmpty(password)) {
			throw new InvalidUserException(TweetConstants.INVALID_PASS_MSG);
		}

		// update the password for the user
		log.info("updating {} password", username);
		userService.forgetPasswordUser(username, password, token);
		log.info("user password updated successfully");

		return new ResponseEntity<>(TweetConstants.UPDATE_PASS_MSG, HttpStatus.OK);
	}

	@GetMapping("/api/v1.0/tweets/users/all")
	public ResponseEntity<MappingJacksonValue> getAllUsers() throws NoUsersFoundException {

		List<UserDoc> users = userService.getAllUsers();
		
		// filter out any other property other than firstname, lastname and username
		SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept("username","firstName","lastName");
		FilterProvider filters = new SimpleFilterProvider().addFilter("UserDocFilter",userFilter);
		MappingJacksonValue usersMapping = new MappingJacksonValue(users);
		usersMapping.setFilters(filters);

		return new ResponseEntity<>(usersMapping, HttpStatus.OK);
	}
}
