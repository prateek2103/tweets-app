package com.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.Tweet;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.service.ITweetService;

/**
 * rest controller to handle rest services call for tweets application
 * 
 * @author prateekpurohit
 *
 */
@RestController
public class TweetsController {

	@Autowired
	private ITweetService tweetService;
	

	/**
	 * rest service to get all tweets for a particular username
	 * @param username
	 * @return
	 * @throws NoTweetsFoundException
	 */
	@GetMapping("/api/v1.0/tweets/{username}")
	public ResponseEntity<List<Tweet>> getTweetsByUsername(@PathVariable String username)
			throws NoTweetsFoundException {

		List<Tweet> tweets = tweetService.getTweetsByUsername(username);

		if (tweets.isEmpty()) {
			throw new NoTweetsFoundException(TweetConstants.TWEETS_NOT_FOUND_MESSAGE);
		}

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tweets);
	}
	
	@PostMapping("/api/v1.0/tweets/{username}/add")
	public ResponseEntity<String> postTweet(@PathVariable String username, @RequestBody Tweet tweet){
		
		tweetService.addTweetForUsername(username,tweet);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("new tweet added");
	}

}
