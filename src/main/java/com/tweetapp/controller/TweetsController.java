package com.tweetapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
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
	public ResponseEntity<MappingJacksonValue> getTweetsByUsername(@RequestHeader("Authorization") String token,
			@PathVariable String username) throws NoTweetsFoundException, InvalidTokenException {

		List<TweetDoc> tweets = tweetService.getTweetsByUsername(token, username);

		// filter out the unnecessary fields in the tweets list
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
	 * 
	 * @param authToken
	 * @return
	 * @throws InvalidTokenException
	 */
	@GetMapping("/api/v1.0/tweets/all")
	public ResponseEntity<MappingJacksonValue> getAllTweets(@RequestHeader("Authorization") String authToken)
			throws InvalidTokenException {
		List<TweetDoc> tweets = tweetService.getAllTweets(authToken);

		// filter out the unnecessary fields in the tweets list
		MappingJacksonValue tweetsMapping = tweetUtil.filterTweetData(tweets);

		return ResponseEntity.status(HttpStatus.OK).body(tweetsMapping);
	}

	/**
	 * method to delete tweet by id
	 * 
	 * @param id
	 * @param username
	 * @param authToken
	 * @return
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 */
	@DeleteMapping("/api/v1.0/tweets/{username}/delete/{id}")
	public ResponseEntity<String> deleteTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String authToken)
			throws InvalidTokenException, NoTweetsFoundException {

		tweetService.deleteTweetById(id, username, authToken);

		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_DEL_MSG);
	}

	/**
	 * method to like tweet by id
	 * 
	 * @param id
	 * @param username
	 * @param token
	 * @return
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTokenException
	 */
	@PostMapping("/api/v1.0/tweets/{username}/like/{id}")
	public ResponseEntity<String> likeTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String token)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException {

		tweetService.likeTweetById(id, username, token);

		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_LIKE_TWEET_MSG);
	}

	/**
	 * method to reply to a tweet
	 * @param id
	 * @param username
	 * @param token
	 * @param tweet
	 * @return
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTokenException
	 * @throws InvalidTweetException
	 */
	@PostMapping("/api/v1.0/tweets/{username}/reply/{id}")
	public ResponseEntity<String> replyTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String token,
			@RequestBody TweetDoc tweet)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException, InvalidTweetException {

		tweetService.replyTweetById(id, username, token, tweet);

		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_REPLY_TWEET_MSG);
	}
}
