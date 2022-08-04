package com.tweetapp.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.model.UserToken;

public interface IUserService extends UserDetailsService {

	/**
	 * authenticates the user and returns jwt token
	 * 
	 * @param userModel
	 * @return userToken
	 * @throws InvalidUserException
	 */
	public UserToken loginUser(UserDoc userModel) throws InvalidUserException;

	/**
	 * method to save new user in the database
	 * 
	 * @param userModel
	 * @throws InvalidUserException
	 */
	public void registerUser(UserDoc userModel) throws InvalidUserException;

	/**
	 * method to change password for logged in user
	 * 
	 * @param username
	 * @param password
	 * @param token
	 * @throws InvalidTokenException
	 */
	public void forgetPasswordUser(String username, String password, String token) throws InvalidTokenException;

	/**
	 * task-1
	 * method to get all users
	 * @return
	 * @throws NoUsersFoundException 
	 */
	public List<UserDoc> getAllUsers() throws NoUsersFoundException;

}
