package com.tweetapp.service;

import java.util.List;

import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.NoTweetsFoundException;

/**
 * service to handle CRUD operations on tweet
 * @author prateekpurohit
 *
 */
public interface ITweetService {
	
	/**
	 * story task-2
	 * service to get all tweets for a particular username
	 * @param username
	 * @return
	 * @throws InvalidTokenException 
	 */
	public List<TweetDoc> getTweetsByUsername(String authToken, String username) throws NoTweetsFoundException, InvalidTokenException;

	/**
	 * service to post tweet for a particular username
	 * @param username
	 */
	public void addTweetForUsername(String username,TweetDoc tweet);
	
	/**
	 * task-2
	 * method to get all tweets
	 * @return
	 * @throws InvalidTokenException 
	 */
	public List<TweetDoc> getAllTweets(String authToken) throws InvalidTokenException;
}
