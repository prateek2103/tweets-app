package com.tweetapp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.model.SecurityUser;
import com.tweetapp.model.UserToken;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.service.IUserService;
import com.tweetapp.util.JwtUtil;
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
	private TweetUtil tweetUtil;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) {
		UserDoc user = userRepository.findByUsername(userName);
		return user == null ? null : new SecurityUser(user);
	}

	/**
	 * method to log in an existing user
	 */
	@Override
	public UserToken loginUser(UserDoc userModel) throws InvalidUserException {

		SecurityUser dbUser = (SecurityUser) loadUserByUsername(userModel.getUsername());

		// incase the username is not found or the password does not match
		if (dbUser == null || !passwordEncoder.matches(userModel.getPassword(), dbUser.getPassword())) {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);

		}

		return jwtUtil.createToken(dbUser.getUser());
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
			
			//encode the password
			user.setPassword(passwordEncoder.encode(user.getPassword()));
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
		
		String tokenUser = jwtUtil.extractUsername(token);

		// if user is valid then get user details and update the password
		if (tokenUser.equals(username)) {
			UserDoc dbUser = userRepository.findByUsername(username);
			dbUser.setPassword(passwordEncoder.encode(password));
			userRepository.save(dbUser);
		} else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
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
		String pattern = "*" + username + "*";
		List<UserDoc> users = userRepository.findByUsernameLike(pattern);

		// in case there are no users in the database
		if (users.isEmpty()) {
			throw new NoUsersFoundException();

		}
		return users;
	}

}
