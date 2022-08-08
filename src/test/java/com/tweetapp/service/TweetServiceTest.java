package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.model.AuthResponse;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.impl.TweetServiceImpl;
import com.tweetapp.util.TweetUtil;

/**
 * task-2 junits for tweetService
 * 
 * @author prateekpurohit
 *
 */
@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

	@Mock
	private ITweetRepository tweetRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private TweetUtil tweetUtil;

	@InjectMocks
	private TweetServiceImpl tweetService;

	private static final String TEST_TOKEN = "testToken";
	private static final String TEST_USER = "testUser";
	private static final String TEST_USER_2 = "testUser2";
	private static final String TEST_ID = "testId";

	private List<TweetDoc> tweets = new ArrayList<>();

	/**
	 * method to test getAllTweets
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getAllTweets() throws InvalidTokenException {

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findAll()).thenReturn(tweets);

		List<TweetDoc> actualResult = tweetService.getAllTweets(TEST_TOKEN);
		List<TweetDoc> expectedResult = tweetRepository.findAll();

		// then
		assertEquals(expectedResult, actualResult);
	}

	/**
	 * method to test getAllTweets throws exception
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getAllTweetsThrowsException() throws InvalidTokenException {

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, false));

		// then
		assertThrows(InvalidTokenException.class, () -> tweetService.getAllTweets(TEST_TOKEN));
	}

	/**
	 * method to test getTweetsByUsername
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getTweetsByUsername() throws NoTweetsFoundException, InvalidTokenException {
		List<TweetDoc> tweets = new ArrayList<>(Arrays.asList(new TweetDoc()));

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findByHandle(TEST_USER)).thenReturn(tweets);

		List<TweetDoc> actualTweets = tweetService.getTweetsByUsername(TEST_TOKEN, TEST_USER);

		// then
		assertEquals(tweets, actualTweets);

	}

	/**
	 * method to test getTweetsByUsername throws exception
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getTweetsByUsernameThrowsException() throws NoTweetsFoundException, InvalidTokenException {
		List<TweetDoc> tweets = new ArrayList<>();

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findByHandle(TEST_USER)).thenReturn(tweets);

		// then
		assertThrows(NoTweetsFoundException.class, () -> tweetService.getTweetsByUsername(TEST_TOKEN, TEST_USER));
	}

	/**
	 * method to test deleteTweetById service
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 */
	@Test
	void test_deleteTweetById() throws InvalidTokenException, NoTweetsFoundException {
		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER);

		Optional<TweetDoc> tweetRes = Optional.of(tweet);

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetRes);

		tweetService.deleteTweetById(TEST_ID, TEST_USER, TEST_TOKEN);

		// then
		verify(tweetRepository, times(1)).delete(tweet);
	}

	/**
	 * method to test _deleteTweetById throws exception when no tweets exists
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_deleteTweetByIdThrowsExceptionWhenTweetNotExists() throws InvalidTokenException {

		Optional<TweetDoc> tweetRes = Optional.empty();

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetRes);

		// then
		assertThrows(NoTweetsFoundException.class, () -> tweetService.deleteTweetById(TEST_ID, TEST_USER, TEST_TOKEN));
	}

	/**
	 * method to test _deleteTweetById throws invalid token exception when the tweet
	 * user is different
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_deleteTweetByIdThrowsInvalidExceptionWhenTweetUserIsDifferent() throws InvalidTokenException {

		TweetDoc tweet = new TweetDoc();
		tweet.setHandle("different user");

		Optional<TweetDoc> tweetRes = Optional.of(tweet);

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetRes);

		// then
		assertThrows(InvalidTokenException.class, () -> tweetService.deleteTweetById(TEST_ID, TEST_USER_2, TEST_TOKEN));
	}

	/**
	 * method to test _deleteTweetById throws Invalid token exception for invalid
	 * token
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_deleteTweetByIdThrowsInvalidExceptionWhenTokenIsInvalid() throws InvalidTokenException {

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, false));

		// then
		assertThrows(InvalidTokenException.class, () -> tweetService.deleteTweetById(TEST_ID, TEST_USER, TEST_TOKEN));
	}

	/**
	 * method to test likeTweetsById
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_likeTweetByIdCallsRepo() throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		TweetDoc tweet = new TweetDoc();
		tweet.setLikesOnTweet(1L);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		tweetService.likeTweetById(TEST_ID, TEST_USER_2, TEST_TOKEN);

		// then
		verify(tweetRepository, times(1)).save(any(TweetDoc.class));
	}

	/**
	 * method to test like tweets by id throws exception when no tweet is found by
	 * that id
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_likeTweetByIdThrowsExceptionOnNoTweet()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		Optional<TweetDoc> tweetOp = Optional.empty();

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(NoTweetsFoundException.class, () -> tweetService.likeTweetById(TEST_ID, TEST_USER_2, TEST_TOKEN));
	}

	/**
	 * method to test like tweets by id throws exception when tweet user is same as
	 * the tweet that is being liked
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_likeTweetByIdThrowsExceptionOnTweetUserSameAsTweet()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		TweetDoc tweet = new TweetDoc();
		tweet.setLikesOnTweet(1L);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(InvalidUserException.class, () -> tweetService.likeTweetById(TEST_ID, TEST_USER, TEST_TOKEN));
	}

	/**
	 * method to test likeTweetById throws exception on invalid token
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_likeTweetByIdThrowsExceptionOnInvalidToken()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, false));

		assertThrows(InvalidTokenException.class, () -> tweetService.likeTweetById(TEST_ID, TEST_USER, TEST_TOKEN));
	}

	/**
	 * test method replyTweetById throws exception on invalid token
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionOnInvalidToken()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		TweetDoc tweetReply = new TweetDoc();

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, false));

		assertThrows(InvalidTokenException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}
	
	/**
	 * test method replyTweetById throws exception when the tweet is not found
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionOnTweetNotPresent()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {
		
		Optional<TweetDoc> tweetOp = Optional.empty();
		TweetDoc tweetReply = new TweetDoc();
		
		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(NoTweetsFoundException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}
	
	/**
	 * test method tweetReplyById throws exception when the username does not match the token's
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdThrowsInvalidUserException()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {
		
		TweetDoc tweet = new TweetDoc();
		Optional<TweetDoc> tweetOp = Optional.of(tweet);
		TweetDoc tweetReply = new TweetDoc();
		
		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER_2, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(InvalidUserException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}
	
	/**
	 * method to test if replyTweetById throws exception when tweet message length exceeds
	 * @throws InvalidTokenException
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionOnLengthExceed() throws InvalidTokenException {

		TweetDoc tweet = new TweetDoc();
		Optional<TweetDoc> tweetOp = Optional.of(tweet);
		TweetDoc tweetReply = new TweetDoc();
		tweetReply.setMessage("a".repeat(145));
		
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);
		
		//then
		assertThrows(InvalidTweetException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}
	
	/**
	 * method to test replyTweetById
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidTweetException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdCallsRepo() throws InvalidTokenException, NoTweetsFoundException, InvalidTweetException, InvalidUserException {
		TweetDoc tweet = new TweetDoc();
		tweet.setReplies(new ArrayList<>());
		
		Optional<TweetDoc> tweetOp = Optional.of(tweet);
		TweetDoc tweetReply = new TweetDoc();
		tweetReply.setMessage("some tweet");
		
		//when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, true));
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);
		
		tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply);
		
		//then
		verify(tweetRepository,times(1)).save(tweetReply);
		verify(tweetRepository,times(1)).save(tweet);
	}

}
