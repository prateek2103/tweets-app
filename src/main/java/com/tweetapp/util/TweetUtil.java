package com.tweetapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;

/**
 * utility class for tweetsApp
 * 
 * @author prateekpurohit
 *
 */
@Component
public class TweetUtil {

	//constants
	private static final String MESSAGE_START = "tweetsApp.users index:";
	private static final String MESSAGE_END = "dup key";
	private static final String TOKEN_PREFIX = "Bearer";
	private static final int ENCODING_STRENGTH = 10;

	/**
	 * method to return the field for dupe check exception
	 * 
	 * @param errMessage
	 * @return
	 */
	public String extractDupeFieldFromErrMsg(String errMessage) {
		int start = errMessage.indexOf(MESSAGE_START);
		int end = errMessage.indexOf(MESSAGE_END);

		return errMessage.substring(start + MESSAGE_START.length(), end).trim();
	}

	/**
	 * method to validate the user before storing in the database
	 * 
	 * @param user
	 * @return
	 * @throws InvalidUserException
	 */
	public boolean validateUserDetails(UserDoc user) throws InvalidUserException {

		// validate phone number
		if (user.getContactNumber() != null && Long.toString(user.getContactNumber()).length() != 10) {
			throw new InvalidUserException(TweetConstants.INVALID_PHONE_NUM_MSG);
		}

		return true;
	}

	/**
	 * method to get the pure token (removing the bearer part from the token)
	 * 
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	public String getPureToken(String token) throws InvalidTokenException {
		if (token != null && token.substring(0, 6).equals(TOKEN_PREFIX) && token.length() > 7)
			return token.substring(7);

		else
			throw new InvalidTokenException();
	}

	/**
	 * method to encode the password before storing
	 * 
	 * @param plainPassword
	 * @return
	 */
	public String encryptPassword(String plainPassword) {

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(ENCODING_STRENGTH);
		return bCryptPasswordEncoder.encode(plainPassword);
	}

	/**
	 * method to compare passwords
	 * 
	 * @param dbPassword
	 * @param userPassword
	 * @return
	 */
	public boolean comparePasswords(String dbPassword, String userPassword) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(ENCODING_STRENGTH);
		return bCryptPasswordEncoder.matches(userPassword, dbPassword);
	}

}
