package com.tweetapp.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.dto.PostTweetDto;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.kafka.TweetEventProducer;
import com.tweetapp.service.ITweetService;
import com.tweetapp.util.JwtUtil;
import com.tweetapp.util.TweetUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * rest controller to handle rest services call for tweets application
 * 
 * @author prateekpurohit
 *
 */
@RestController
@Tag(name="tweet services",description = "api endpoints for tweet related services")
public class TweetsController {

	@Autowired
	private ITweetService tweetService;

	@Autowired
	private TweetUtil tweetUtil;

	@Autowired
	private TweetEventProducer tweetEventProducer;
	
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * rest service to get all tweets for a particular username
	 * 
	 * @param username
	 * @return
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@GetMapping("/{username}")
	public ResponseEntity<MappingJacksonValue> getTweetsByUsername(
			@PathVariable String username) throws NoTweetsFoundException, InvalidTokenException {

		List<TweetDoc> tweets = tweetService.getTweetsByUsername(username);

		// filter out the unnecessary fields in the tweets list
		MappingJacksonValue tweetsMapping = tweetUtil.filterTweetData(tweets);

		return ResponseEntity.status(HttpStatus.OK).body(tweetsMapping);
	}

	/**
	 * method to post a new tweet by a user
	 * 
	 * @param username
	 * @param tweet
	 * @return
	 * @throws JsonProcessingException
	 * @throws InvalidTokenException
	 */
	@PostMapping("/{username}/add")
	public ResponseEntity<String> postTweet(@PathVariable String username, @RequestBody PostTweetDto tweet,
			@RequestHeader("Authorization") String authToken) throws JsonProcessingException, InvalidTokenException {

		String tokenUsername = jwtUtil.extractUsername(authToken);
		
		TweetDoc tweetDoc = new TweetDoc();
		
		if(tokenUsername.equals(username)) {
			tweetDoc.setMessage(tweet.getTweetMessage());
			tweetDoc.setHandle(username);
			tweetDoc.setCreatedAt(new Date());
			tweetEventProducer.sendTweetEvent(tweetDoc);
			return ResponseEntity.status(HttpStatus.CREATED).body(TweetConstants.SUCCESS_CREATE_TWEET_MSG);
		}
		else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}
		
	}

	/**
	 * method to get all tweets
	 * 
	 * @param authToken
	 * @return
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException 
	 */
	@GetMapping("/all")
	public ResponseEntity<MappingJacksonValue> getAllTweets()
			throws InvalidTokenException, NoTweetsFoundException {
		List<TweetDoc> tweets = tweetService.getAllTweets();

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
	@DeleteMapping("/{username}/delete/{id}")
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
	@PostMapping("/{username}/like/{id}")
	public ResponseEntity<String> likeTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String token)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException {

		tweetService.likeTweetById(id, username, token);

		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_LIKE_TWEET_MSG);
	}

	/**
	 * method to reply to a tweet
	 * 
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
	@PostMapping("/{username}/reply/{id}")
	public ResponseEntity<String> replyTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String token,
			@RequestBody TweetDoc tweet)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException, InvalidTweetException {

		tweetService.replyTweetById(id, username, token, tweet);

		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_REPLY_TWEET_MSG);
	}

	/**
	 * method to update a tweet by id
	 * 
	 * @param id
	 * @param username
	 * @param token
	 * @param tweet
	 * @return
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTweetException
	 */
	@PutMapping("/{username}/update/{id}")
	public ResponseEntity<String> updateTweetById(@PathVariable("id") String id,
			@PathVariable("username") String username, @RequestHeader("Authorization") String token,
			@RequestBody TweetDoc tweet)
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException, InvalidTweetException {

		tweetService.updateTweetById(id, username, token, tweet);
		return ResponseEntity.status(HttpStatus.OK).body(TweetConstants.SUCCESS_UPDATE_TWEET);
	}
}
