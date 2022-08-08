package com.tweetapp.service;

import java.util.List;

import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
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
	
	/**
	 * method to delete tweet by id
	 * @param id
	 * @param token
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException 
	 */
	public void deleteTweetById(String id, String username, String token) throws InvalidTokenException, NoTweetsFoundException;
	
	/**
	 * method to like tweet by id
	 * 
	 * @param id
	 * @param username
	 * @param token
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTokenException
	 */
	public void likeTweetById(String id, String username, String token) throws NoTweetsFoundException, InvalidUserException, InvalidTokenException;

	/**
	 * method to reply to tweet by id
	 * 
	 * @param id
	 * @param username
	 * @param token
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidTweetException
	 * @throws InvalidUserException 
	 */
	public void replyTweetById(String id, String username, String token, TweetDoc tweet) throws InvalidTokenException, NoTweetsFoundException, InvalidTweetException, InvalidUserException;
}
