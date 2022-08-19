package com.tweetapp.util;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidUserException;

/**
 * utility class for tweetsApp
 * 
 * @author prateekpurohit
 *
 */
@Component
public class TweetUtil {

	// constants
	private static final String MESSAGE_START = "tweetsApp.users index:";
	private static final String MESSAGE_END = "dup key";

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
	 * method to filter tweets data
	 * 
	 * @param tweets
	 * @return
	 */
	public MappingJacksonValue filterTweetData(List<TweetDoc> tweets) {// filter out the unnecessary properties
		SimpleBeanPropertyFilter tweetFilter = SimpleBeanPropertyFilter.filterOutAllExcept("handle", "message", "id",
				"createdAt", "avatarUrl", "likesOnTweet","replies");

		FilterProvider filters = new SimpleFilterProvider().addFilter("TweetDocFilter", tweetFilter);

		MappingJacksonValue tweetsMapping = new MappingJacksonValue(tweets);

		tweetsMapping.setFilters(filters);

		return tweetsMapping;

	}

	/**
	 * method to filter user data
	 * 
	 * @param tweets
	 * @return
	 */
	public MappingJacksonValue filterUserData(List<UserDoc> users) {

		// filter out the unnecessary properties
		SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept("username", "firstName",
				"lastName");

		FilterProvider filters = new SimpleFilterProvider().addFilter("UserDocFilter", userFilter);

		MappingJacksonValue usersMapping = new MappingJacksonValue(users);

		usersMapping.setFilters(filters);

		return usersMapping;

	}

}
