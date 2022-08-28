package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.service.impl.TweetServiceImpl;
import com.tweetapp.util.JwtUtil;
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

	@Mock 
	private IUserRepository userRepository;
	
	@InjectMocks
	private TweetServiceImpl tweetService;

	private static final String TEST_TOKEN = "testToken";
	private static final String TEST_USER = "testUser";
	private static final String TEST_USER_2 = "testUser2";
	private static final String TEST_ID = "testId";

	/**
	 * method to test getAllTweets
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 */
	@Test
	void test_getAllTweetsCallsRepo() throws InvalidTokenException, NoTweetsFoundException {

		// when
		when(tweetRepository.findAll()).thenReturn(Arrays.asList(new TweetDoc()));

		tweetService.getAllTweets();

		// then
		verify(tweetRepository, times(1)).findAll();
	}

	/**
	 * method to test getAllTweets throws exception
	 */
	@Test
	void test_getAllTweetsThrowsException() {

		// when
		when(tweetRepository.findAll()).thenReturn(new ArrayList<>());

		// then
		assertThrows(NoTweetsFoundException.class, () -> tweetService.getAllTweets());
	}

	/**
	 * method to test getTweetsByUsername
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getTweetsByUsernameCallsRepo() throws NoTweetsFoundException, InvalidTokenException {

		// when
		when(tweetRepository.findByHandle(TEST_USER)).thenReturn(Arrays.asList(new TweetDoc()));

		tweetService.getTweetsByUsername(TEST_USER);

		// then
		verify(tweetRepository, times(1)).findByHandle(TEST_USER);

	}

	/**
	 * method to test getTweetsByUsername throws exception
	 * 
	 * @throws NoTweetsFoundException
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getTweetsByUsernameThrowsException() throws NoTweetsFoundException, InvalidTokenException {
		// when
		when(tweetRepository.findByHandle(TEST_USER)).thenReturn(new ArrayList<>());

		// then
		assertThrows(NoTweetsFoundException.class, () -> tweetService.getTweetsByUsername(TEST_USER));
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetRes);

		// then
		assertThrows(BadCredentialsException.class, () -> tweetService.deleteTweetById(TEST_ID, TEST_USER_2, TEST_TOKEN));
	}
	
	/**
	 * method to test _deleteTweetById throws invalid token exception when the api username parameter
	 * is different than token's username
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_deleteTweetByIdThrowsInvalidExceptionWhenParamUserIsDifferent() throws InvalidTokenException {

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);

		// then
		assertThrows(BadCredentialsException.class, () -> tweetService.deleteTweetById(TEST_ID, TEST_USER_2, TEST_TOKEN));
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
		tweet.setHandle(TEST_USER);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);
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
		tweet.setHandle(TEST_USER);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);

		assertThrows(BadCredentialsException.class, () -> tweetService.likeTweetById(TEST_ID, TEST_USER, TEST_TOKEN));
	}

	/**
	 * test method replyTweetById throws exception on invalid token
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionOnInvalidToken()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		TweetDoc tweetReply = new TweetDoc();

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);

		assertThrows(BadCredentialsException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}

	/**
	 * test method replyTweetById throws exception when the tweet is not found
	 * 
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
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(NoTweetsFoundException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}


	/**
	 * method to test if replyTweetById throws exception when tweet message length
	 * exceeds
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionOnLengthExceed() throws InvalidTokenException {

		TweetDoc tweet = new TweetDoc();
		Optional<TweetDoc> tweetOp = Optional.of(tweet);
		TweetDoc tweetReply = new TweetDoc();
		tweetReply.setMessage("a".repeat(145));

		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		// then
		assertThrows(InvalidTweetException.class,
				() -> tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply));
	}

	/**
	 * method to test replyTweetById
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidTweetException
	 * @throws InvalidUserException
	 */
	@Test
	void test_replyTweetByIdCallsRepo()
			throws InvalidTokenException, NoTweetsFoundException, InvalidTweetException, InvalidUserException {
		TweetDoc tweet = new TweetDoc();
		tweet.setReplies(new ArrayList<>());

		Optional<TweetDoc> tweetOp = Optional.of(tweet);
		TweetDoc tweetReply = new TweetDoc();
		tweetReply.setMessage("some tweet");

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);
		when(userRepository.findByUsername(any(String.class))).thenReturn(new UserDoc());

		tweetService.replyTweetById(TEST_ID, TEST_USER, TEST_TOKEN, tweetReply);

		// then
		verify(tweetRepository, times(1)).save(tweetReply);
		verify(tweetRepository, times(1)).save(tweet);
	}

	/**
	 * method to test updateTweetById throws exception on invalid token
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionOnInvalidToken()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		TweetDoc updateTweet = new TweetDoc();

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);

		assertThrows(BadCredentialsException.class,
				() -> tweetService.updateTweetById(TEST_ID, TEST_USER, TEST_TOKEN, updateTweet));
	}

	/**
	 * test method updateTweetById throws exception on tweet not present
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionOnTweetNotPresent()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException {

		Optional<TweetDoc> tweetOp = Optional.empty();
		TweetDoc updateTweet = new TweetDoc();

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		assertThrows(NoTweetsFoundException.class,
				() -> tweetService.updateTweetById(TEST_ID, TEST_USER, TEST_TOKEN, updateTweet));
	}

	/**
	 * test method updateTweetById throws exception when message length exceeds 144
	 * characters.
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionOnLengthExceed() throws InvalidTokenException {

		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		TweetDoc updateTweet = new TweetDoc();
		updateTweet.setMessage("a".repeat(145));

		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		// then
		assertThrows(InvalidTweetException.class,
				() -> tweetService.updateTweetById(TEST_ID, TEST_USER, TEST_TOKEN, updateTweet));
	}


	/**
	 * method to test updateTweetById throws exception on tweet username different
	 * than the given user
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionOnInvalidUserWithDifferentTweetUsername() throws InvalidTokenException {

		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER_2);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		TweetDoc updateTweet = new TweetDoc();
		updateTweet.setMessage("a".repeat(142));

		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		// then
		assertThrows(InvalidUserException.class,
				() -> tweetService.updateTweetById(TEST_ID, TEST_USER, TEST_TOKEN, updateTweet));
	}

	/**
	 * method to test updateTweetById
	 * 
	 * @throws InvalidTokenException
	 * @throws NoTweetsFoundException
	 * @throws InvalidUserException
	 * @throws InvalidTweetException
	 */
	@Test
	void test_updateTweetByIdCallsRepo()
			throws InvalidTokenException, NoTweetsFoundException, InvalidUserException, InvalidTweetException {

		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER);
		Optional<TweetDoc> tweetOp = Optional.of(tweet);

		TweetDoc updateTweet = new TweetDoc();
		updateTweet.setMessage("a".repeat(142));

		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(tweetRepository.findById(TEST_ID)).thenReturn(tweetOp);

		tweetService.updateTweetById(TEST_ID, TEST_USER, TEST_TOKEN, updateTweet);

		// then
		verify(tweetRepository, times(1)).save(any(TweetDoc.class));
	}

	/**
	 * method to test addTweet
	 * 
	 * @throws InvalidTweetException
	 */
	@Test
	void test_postTweet() throws InvalidTweetException {
		TweetDoc tweet = new TweetDoc();
		tweet.setMessage("any message");

		// when
		tweetService.addTweet(tweet);

		// then
		verify(tweetRepository, times(1)).save(tweet);
	}

	/**
	 * method to test addTweet throws exception when message limit exceeds
	 * 
	 * @throws InvalidTweetException
	 */
	@Test
	void test_postTweetThrowsException() throws InvalidTweetException {
		TweetDoc tweet = new TweetDoc();
		tweet.setMessage("a".repeat(145));

		// assert
		assertThrows(InvalidTweetException.class, () -> tweetService.addTweet(tweet));
	}
}
