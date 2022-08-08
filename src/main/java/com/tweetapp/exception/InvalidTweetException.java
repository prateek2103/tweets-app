package com.tweetapp.exception;

public class InvalidTweetException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidTweetException(String message) {
		super(message);
	}

}
