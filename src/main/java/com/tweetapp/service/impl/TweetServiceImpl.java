package com.tweetapp.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.model.AuthResponse;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.ITweetService;
import com.tweetapp.util.TweetUtil;

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
	private TweetUtil tweetUtil;

	/**
	 * method to retrieve all tweets by username
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Override
	public List<TweetDoc> getTweetsByUsername(String authToken, String username)
			throws NoTweetsFoundException, InvalidTokenException {
		AuthResponse authResponse = tweetUtil.getValidity(authToken);

		if (authResponse.isValid()) {
			List<TweetDoc> tweets = tweetRepository.findByHandle(username);

			if (tweets.isEmpty()) {
				throw new NoTweetsFoundException(TweetConstants.TWEETS_NOT_FOUND_MESSAGE);
			}

			return tweets;
		} else {
			throw new InvalidTokenException();
		}

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
	 * task-2 method to get all tweets
	 * 
	 * @throws InvalidTokenException
	 */
	@Override
	public List<TweetDoc> getAllTweets(String authToken) throws InvalidTokenException {
		AuthResponse authResponse = tweetUtil.getValidity(authToken);

		if (authResponse.isValid()) {
			return tweetRepository.findAll();
		} else {
			throw new InvalidTokenException();
		}

	}

	/**
	 * task-3 method to delete tweet by id
	 * 
	 * @throws NoTweetsFoundException
	 */
	@Override
	public void deleteTweetById(String id, String username, String authToken)
			throws InvalidTokenException, NoTweetsFoundException {
		AuthResponse authResponse = tweetUtil.getValidity(authToken);

		if (authResponse.isValid()) {

			// get details about the tweet
			Optional<TweetDoc> tweet = tweetRepository.findById(id);

			// if tweet does not exist
			if (!tweet.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			} else {

				// match the tweet user with the token user before deleting
				if (!username.equals(authResponse.getUsername()))
					throw new InvalidTokenException();

				tweetRepository.delete(tweet.get());

			}
		}

		else {
			throw new InvalidTokenException();
		}

	}

	@Override
	public void likeTweetById(String id, String username, String token)
			throws NoTweetsFoundException, InvalidUserException, InvalidTokenException {
		AuthResponse authResponse = tweetUtil.getValidity(token);

		if (authResponse.isValid()) {
			Optional<TweetDoc> tweet = tweetRepository.findById(id);

			// if tweet does not exist
			if (!tweet.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			}

			// if the user posted the tweet
			if (username.equals(authResponse.getUsername())) {
				throw new InvalidUserException(TweetConstants.USER_NOT_LIKE_MSG);
			}

			// else
			TweetDoc tweetResp = tweet.get();
			tweetResp.setLikesOnTweet(tweetResp.getLikesOnTweet() + 1);
			tweetRepository.save(tweetResp);

		} else {
			throw new InvalidTokenException();
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
		
		AuthResponse authResponse = tweetUtil.getValidity(token);
		
		if (authResponse.isValid()) {
			Optional<TweetDoc> tweetOp = tweetRepository.findById(id);

			//if tweet is not present
			if (!tweetOp.isPresent()) {
				throw new NoTweetsFoundException(TweetConstants.TWEET_NOT_EXIST_MSG);
			}

			if(!username.equals(authResponse.getUsername())) {
				throw new InvalidUserException(TweetConstants.INVALID_USER_DETAILS);
			}
			
			//if the tweet exceeds 144 characters
			if(tweetReply.getMessage().length()>144) {
				throw new InvalidTweetException(TweetConstants.TWEET_LIMIT_EXCEED);
			}
			
			//else
			TweetDoc realTweet = tweetOp.get();
			List<TweetDoc> replies = realTweet.getReplies();
			replies.add(tweetReply);
			
			//save the reply
			tweetRepository.save(tweetReply);
			
			//save the reply ref in real tweet
			tweetRepository.save(realTweet);
		}
		
		else {
			throw new InvalidTokenException();
		}

	}

}
