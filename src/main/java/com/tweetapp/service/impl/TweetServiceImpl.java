package com.tweetapp.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.model.AuthResponse;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.ITweetService;
import com.tweetapp.util.TweetUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetServiceImpl implements ITweetService {

	@Autowired
	private ITweetRepository tweetRepository;
	
	@Autowired
	private TweetUtil tweetUtil;
	
	/**
	 * method to retrieve all tweets by username
	 */
	@Override
	public List<TweetDoc> getTweetsByUsername(String username) {
		
		String handle = String.format("@%s", username);
		log.info("retrieving tweets for the username");
		
		List<TweetDoc> tweets = tweetRepository.findByHandle(handle);
		
		if(tweets.isEmpty()) {
			log.info("no tweets found for this user");
		}
		
		return tweets;
	}

	/**
	 * method to post a tweet for a particular username
	 */
	@Override
	public void addTweetForUsername(String username, TweetDoc tweet) {
		
		tweet.setHandle(username);
		tweet.setAvatarUrl("some url");
		tweet.setCreatedAt(new Date());
		tweet.setLikesOnTweet(1l);
		tweet.setMessage("new message");
		tweet.setReply(false);
		
		TweetDoc reply = new TweetDoc();
		reply.setMessage("reply message");
		reply.setCreatedAt(new Date());
		
		tweetRepository.save(reply);
		
		tweet.setReplies(Arrays.asList(reply));
		
		tweetRepository.save(tweet);
	}

	/**
	 * task-2
	 * method to get all tweets
	 * @throws InvalidTokenException 
	 */
	@Override
	public List<TweetDoc> getAllTweets(String authToken) throws InvalidTokenException {
		AuthResponse authResponse = tweetUtil.getValidity(authToken);
		
		if(authResponse.isValid()) {
			return tweetRepository.findAll();
		}
		else {
			throw new InvalidTokenException();
		}
		
	}

}
