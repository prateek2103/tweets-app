package com.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.service.ITweetService;
import com.tweetapp.util.TweetUtil;

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
	
	@Autowired
	private TweetUtil tweetUtil;

	/**
	 * rest service to get all tweets for a particular username
	 * 
	 * @param username
	 * @return
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException 
	 */
	@GetMapping("/api/v1.0/tweets/{username}")
	public ResponseEntity<MappingJacksonValue> getTweetsByUsername(@RequestHeader("Authorization") String token, @PathVariable String username)
			throws NoTweetsFoundException, InvalidTokenException {

		List<TweetDoc> tweets = tweetService.getTweetsByUsername(token, username);

		//filter out the unnecessary fields in the tweets list
		MappingJacksonValue tweetsMapping = tweetUtil.filterTweetData(tweets);

		return ResponseEntity.status(HttpStatus.OK).body(tweetsMapping);
	}

	@PostMapping("/api/v1.0/tweets/{username}/add")
	public ResponseEntity<String> postTweet(@PathVariable String username, @RequestBody TweetDoc tweet) {

		tweetService.addTweetForUsername(username, tweet);

		return ResponseEntity.status(HttpStatus.CREATED).body("new tweet added");
	}

	/**
	 * method to get all tweets
	 * @param authToken
	 * @return
	 * @throws InvalidTokenException
	 */
	@GetMapping("/api/v1.0/tweets/all")
	public ResponseEntity<MappingJacksonValue> getAllTweets(@RequestHeader("Authorization") String authToken)
			throws InvalidTokenException {
		List<TweetDoc> tweets = tweetService.getAllTweets(authToken);

		//filter out the unnecessary fields in the tweets list
		MappingJacksonValue tweetsMapping = tweetUtil.filterTweetData(tweets);

		return ResponseEntity.status(HttpStatus.OK).body(tweetsMapping);
	}
}
