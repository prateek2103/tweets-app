package com.tweetapp.constants;

/**
 * class to hold all constants for the application
 * 
 * @author prateekpurohit
 *
 */
public class TweetConstants {

	private TweetConstants() {
		throw new IllegalStateException("Utility class");
	}

	//OK status description
	public static final String TWEETS_USER_CREATED_MESSAGE = "new user created successfully";
	public static final String UPDATE_PASS_MSG = "password is updated sucessfully";
	public static final String GET_ALL_USERS_MSG = "all users retrieved successfully";
	public static final String SUCCESS_DEL_MSG = "tweet deleted successfully";
	public static final String SUCCESS_LIKE_TWEET_MSG = "tweet liked successfully";
	public static final String SUCCESS_REPLY_TWEET_MSG = "replied to tweet successfully";
	public static final String SUCCESS_UPDATE_TWEET = "tweet updated successfully";
	public static final String SUCCESS_CREATE_TWEET_MSG = "tweet added successfully";
	
	//error message description
	public static final String TWEETS_NOT_FOUND_MESSAGE = "no tweets found for this username";
	public static final String DUPE_KEY_MSG = "A user with that %s already exists";
	public static final String JSON_PARSE_ERR_MSG = "the request is invalid";
	public static final String INVALID_USER_DETAILS = "user details are not valid";
	public static final String UNAUTHORIZED_USER_ACCESS_MSG = "invalid credentials";
	public static final String INVALID_TOKEN_MSG = "invalid token passed in the request";
	public static final String INVALID_PASS_MSG = "paswword cannot be empty";
	public static final String INVALID_PHONE_NUM_MSG = "the phone number should have 10 digits";
	public static final String NO_USERS_FOUND_MSG = "no users found";
	public static final String TWEET_NOT_EXIST_MSG = "tweet does not exist";
	public static final String USER_NOT_LIKE_MSG = "user cannot like their own tweets";
	public static final String TWEET_LIMIT_EXCEED = "tweet cannot exceed 144 characters";
	public static final String TOKEN_NOT_PASSED_MSG = "auth token is not present";
	
	//constants
	public static final String BASE_PATH = "src/test/resources/";
	public enum REQUEST_TYPE {
		GET_VALID_REQUEST, GET_INVALID_REQUEST
	}
	
	public static final String JWT_HEADER = "Authorization";
	public static final String JWT_KEY = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
	public static final String USERNAME_CLAIM = "username";
}
