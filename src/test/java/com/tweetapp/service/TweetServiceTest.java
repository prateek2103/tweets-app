package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.model.AuthResponse;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.service.impl.TweetServiceImpl;
import com.tweetapp.util.TweetUtil;

/**
 * task-2
 * junits for tweetService
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
	private static final String TEST_USER = "testToken";
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
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getAllTweetsThrowsException() throws InvalidTokenException {

		// when
		when(tweetUtil.getValidity(TEST_TOKEN)).thenReturn(new AuthResponse(TEST_USER, false));

		//then
		assertThrows(InvalidTokenException.class, () -> tweetService.getAllTweets(TEST_TOKEN));
	}
}
