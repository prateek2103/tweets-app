package com.tweetapp.exception;

/**
 * exception class for invalid user data
 * 
 * @author prateekpurohit
 *
 */
public class InvalidUserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUserException(String message) {
		super(message);
	}

}
