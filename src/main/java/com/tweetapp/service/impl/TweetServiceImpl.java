package com.tweetapp.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.document.Tweet;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.ITweetService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetServiceImpl implements ITweetService {

	@Autowired
	private ITweetRepository tweetRepository;
	
	/**
	 * method to retrieve all tweets by username
	 */
	@Override
	public List<Tweet> getTweetsByUsername(String username) {
		
		String handle = String.format("@%s", username);
		log.info("retrieving tweets for the username");
		
		List<Tweet> tweets = tweetRepository.findByHandle(handle);
		
		if(tweets.isEmpty()) {
			log.info("no tweets found for this user");
		}
		
		return tweets;
	}

	/**
	 * method to post a tweet for a particular username
	 */
	@Override
	public void addTweetForUsername(String username, Tweet tweet) {
		
		tweet.setHandle(username);
		tweet.setAvatarUrl("some url");
		tweet.setCreatedAt(new Date());
		tweet.setLikesOnTweet(1l);
		tweet.setMessage("new message");
		tweet.setReply(false);
		
		Tweet reply = new Tweet();
		reply.setMessage("reply message");
		reply.setCreatedAt(new Date());
		
		tweetRepository.save(reply);
		
		tweet.setReplies(Arrays.asList(reply));
		
		tweetRepository.save(tweet);
	}

}
