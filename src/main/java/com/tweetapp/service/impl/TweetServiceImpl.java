package com.tweetapp.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.ITweetService;
import com.tweetapp.util.JwtUtil;

/**
 * service class to handle tweet related operations
 * 
 * @author prateekpurohit
 *
 */
@Service
public class TweetServiceImpl implements ITweetService {

	@Autowired
	private ITweetRepository tweetRepository;
	
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * method to retrieve all tweets by username
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Override
	public List<TweetDoc> getTweetsByUsername(String username) throws NoTweetsFoundException, InvalidTokenException {

		List<TweetDoc> tweets = tweetRepository.findByHandle(username);

		if (tweets.isEmpty()) {
			throw new NoTweetsFoundException(TweetConstants.TWEETS_NOT_FOUND_MESSAGE);
		}

		return tweets;
	}

	/**
	 * method to post a tweet for a particular username
	 * 
	 * @throws InvalidTweetException
	 */
	@Override
	public void addTweet(TweetDoc tweet) throws InvalidTweetException {

		if (tweet.getMessage().length() > 144) {
			throw new InvalidTweetException(TweetConstants.TWEET_LIMIT_EXCEED);
		}
		tweet.setCreatedAt(new Date());
		tweetRepository.save(tweet);
	}

	/**
	 * task-2 method to get all tweets
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException 
	 */
	@Override
	public List<TweetDoc> getAllTweets() throws InvalidTokenException, NoTweetsFoundException {
		List<TweetDoc> tweets = tweetRepository.findAll();
		
		if (tweets.isEmpty()) {
			throw new NoTweetsFoundException(TweetConstants.TWEETS_NOT_FOUND_MESSAGE);
		}

		return tweets;

	}

	/**
	 * task-3 method to delete tweet by id
	 * 
	 * @throws NoTweetsFoundException
	 */
	@Override
	public void deleteTweetById(String id, String username, String authToken)
			throws InvalidTokenException, NoTweetsFoundException {
		
		String tokenUsername = jwtUtil.extractUsername(authToken);

		if (tokenUsername.equals(username)) {

			// get details about the tweet
			Optional<TweetDoc> tweet = tweetRepository.findById(id);

			// if tweet does not exist
			if (!tweet.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			} 
			
			if(!tweet.get().getHandle().equals(tokenUsername)) {
				throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
			}
			
			tweetRepository.delete(tweet.get());

		}
		else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}

	}

	@Override
	public void likeTweetById(String id, String username, String token)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException {
		
		String tokenUsername = jwtUtil.extractUsername(token);

		if (tokenUsername.equals(username)) {
			
			Optional<TweetDoc> tweet = tweetRepository.findById(id);

			// if tweet does not exist
			if (!tweet.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			}

			// if the user posted the tweet
			if (username.equals(tweet.get().getHandle())) {
				throw new InvalidUserException(TweetConstants.USER_NOT_LIKE_MSG);
			}

			// else
			TweetDoc tweetResp = tweet.get();
			tweetResp.setLikesOnTweet(tweetResp.getLikesOnTweet() + 1);
			tweetRepository.save(tweetResp);

		} else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}

	}

	/**
	 * method to reply to tweet by id
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidTweetException
	 * @throws InvalidUserException
	 */
	@Override
	public void replyTweetById(String id, String username, String token, TweetDoc tweetReply)
			throws InvalidTokenException, NoTweetsFoundException, InvalidTweetException, InvalidUserException {

		String tokenUsername = jwtUtil.extractUsername(token);

		if (tokenUsername.equals(username)) {
			Optional<TweetDoc> tweetOp = tweetRepository.findById(id);

			// if tweet is not present
			if (!tweetOp.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			}

			// if the tweet exceeds 144 characters
			if (tweetReply.getMessage().length() > 144) {
				throw new InvalidTweetException(TweetConstants.TWEET_LIMIT_EXCEED);
			}

			// else
			TweetDoc realTweet = tweetOp.get();
			List<TweetDoc> replies = realTweet.getReplies();
			replies.add(tweetReply);

			// save the reply
			tweetRepository.save(tweetReply);

			// save the reply ref in real tweet
			tweetRepository.save(realTweet);
		}

		else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}

	}

	/**
	 * method to update tweet by id
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTweetException
	 */
	@Override
	public void updateTweetById(String id, String username, String token, TweetDoc updateTweet)
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException, InvalidTweetException {

		String tokenUsername = jwtUtil.extractUsername(token);

		if (tokenUsername.equals(username)) {
			Optional<TweetDoc> tweetOp = tweetRepository.findById(id);

			// if tweet is not present
			if (!tweetOp.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			}

			// if the username is different from the token or the tweet belongs to a
			// different user
			if (!username.equals(tweetOp.get().getHandle())) {
				throw new InvalidUserException(TweetConstants.INVALID_USER_DETAILS);
			}

			if (updateTweet.getMessage().length() > 144) {
				throw new InvalidTweetException(TweetConstants.TWEET_LIMIT_EXCEED);
			}

			// update the tweet
			TweetDoc realTweet = tweetOp.get();
			realTweet.setMessage(updateTweet.getMessage());
			tweetRepository.save(realTweet);

		} else {
			throw new BadCredentialsException(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG);
		}

	}

}
