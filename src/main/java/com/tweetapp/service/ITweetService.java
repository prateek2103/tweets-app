package com.tweetapp.service;

import java.util.List;

import com.tweetapp.document.Tweet;

/**
 * service to handle CRUD operations on tweet
 * @author prateekpurohit
 *
 */
public interface ITweetService {
	
	/**
	 * service to get all tweets for a particular username
	 * @param username
	 * @return
	 */
	public List<Tweet> getTweetsByUsername(String username);

	/**
	 * service to post tweet for a particular username
	 * @param username
	 */
	public void addTweetForUsername(String username,Tweet tweet);
}
