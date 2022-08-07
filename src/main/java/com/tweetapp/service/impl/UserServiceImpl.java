package com.tweetapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.model.AuthResponse;
import com.tweetapp.model.UserToken;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.service.IUserService;
import com.tweetapp.util.TweetUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * service class for user related tasks
 * 
 * @author prateekpurohit
 *
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TweetUtil tweetUtil;

	@Override
	public UserDetails loadUserByUsername(String userName) {
		UserDoc user = userRepository.findByUsername(userName);
		return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
	}

	/**
	 * method to authenticate the user and generate jwt token
	 * 
	 * @throws InvalidUserException
	 */
	public UserToken loginUser(UserDoc user) throws InvalidUserException {

		// get the user from the database
		final UserDetails userDetails = loadUserByUsername(user.getUsername());

		UserToken userToken = new UserToken();

		// if the password matches
		if (tweetUtil.comparePasswords(userDetails.getPassword(), user.getPassword())) {

			log.info("password authentication successful");

			// set the values for the token
			userToken.setUsername(user.getUsername());
			userToken.setAuthToken(jwtUtil.generateToken(userDetails));
			return userToken;

		} else {

			log.error("invalid login credentials");
			throw new InvalidUserException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}
	}

	/**
	 * method to save a new user
	 * 
	 * @throws InvalidUserException
	 */
	@Override
	public void registerUser(UserDoc user) throws InvalidUserException {

		// validate the user details before saving in database
		if (tweetUtil.validateUserDetails(user)) {
			log.info("user info validated successfully");
			user.setPassword(tweetUtil.encryptPassword(user.getPassword()));
			userRepository.save(user);
		} else {
			throw new InvalidUserException(TweetConstants.INVALID_USER_DETAILS);
		}
	}

	/**
	 * method to update the password for a logged in user
	 */
	@Override
	public void forgetPasswordUser(String username, String password, String token) throws InvalidTokenException {
		AuthResponse userValidity = tweetUtil.getValidity(token);

		// if user is valid then get user details and update the password
		if (userValidity.isValid() && userValidity.getUsername().equals(username)) {
			log.info("user identity is valid proceeding with updation of password");
			UserDoc user = userRepository.findByUsername(username);
			user.setPassword(tweetUtil.encryptPassword(password));
			userRepository.save(user);
		} else {
			throw new InvalidTokenException();
		}

	}

	/**
	 * task-1 method to get all users from the database
	 * 
	 * @throws NoUsersFoundException
	 */
	@Override
	public List<UserDoc> getAllUsers() throws NoUsersFoundException {

		List<UserDoc> users = userRepository.findAll();

		// in case there are no users in the database
		if (users.isEmpty()) {
			throw new NoUsersFoundException();

		}

		return users;
	}

	/**
	 * task-1 metod to get users by username
	 */
	@Override
	public List<UserDoc> getUsersByUsername(String username) throws NoUsersFoundException {
		String pattern = "*"+username+"*";
		List<UserDoc> users = userRepository.findByUsernameLike(pattern);

		// in case there are no users in the database
		if (users.isEmpty()) {
			throw new NoUsersFoundException();

		}
		return users;
	}

}
