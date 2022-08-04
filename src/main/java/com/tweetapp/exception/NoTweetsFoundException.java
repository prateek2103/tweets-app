package com.tweetapp.exception;

/**
 * exception class for tweets not found
 * @author prateekpurohit
 *
 */
public class NoTweetsFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoTweetsFoundException(String message) {
		super(message);
	}

}
