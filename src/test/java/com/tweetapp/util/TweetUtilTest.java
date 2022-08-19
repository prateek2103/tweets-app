package com.tweetapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidUserException;

@ExtendWith(MockitoExtension.class)
class TweetUtilTest {

	@InjectMocks
	private TweetUtil tweetUtil;
	
	@Mock
	private JwtUtil jwtUtil;

	private static final Long TEST_VALID_PHONE = 1234567890L;
	private static final Long TEST_INVALID_PHONE = 1234L;

	@Test
	void test_extractDupeFieldFromErrMsg() {

		String test_message = "tweetsApp.users index: username dup key";
		String actualResult = tweetUtil.extractDupeFieldFromErrMsg(test_message);
		String expectedResult = "username";

		// asserts
		assertEquals(expectedResult, actualResult);
	}

	@Test
	void test_validateUserDetails() throws InvalidUserException {
		UserDoc user = new UserDoc();
		user.setContactNumber(TEST_VALID_PHONE);

		// then
		assertTrue(tweetUtil.validateUserDetails(user));
	}

	@Test
	void test_validateUserDetailsThrowsException() throws InvalidUserException {
		UserDoc user = new UserDoc();
		user.setContactNumber(TEST_INVALID_PHONE);

		// then
		Exception exception = assertThrows(InvalidUserException.class, () -> tweetUtil.validateUserDetails(user));
		assertEquals(TweetConstants.INVALID_PHONE_NUM_MSG, exception.getMessage());
	}

	
	@Test
	void test_filterTweetData() {
		TweetDoc tweet = new TweetDoc();
		tweet.setAvatarUrl("url");
		tweet.setCreatedAt(new Date());
		tweet.setHandle("username");
		tweet.setId("123");
		tweet.setLikesOnTweet(1L);
		tweet.setMessage("this is a dummy message");
		tweet.setReplies(Arrays.asList(new TweetDoc()));
		tweet.setReply(true);
		
		MappingJacksonValue result = tweetUtil.filterTweetData(Arrays.asList(tweet));
		assertNotNull(result);
	}
	
	@Test
	void test_filterUserData() {
		UserDoc user = new UserDoc();
		user.setContactNumber(TEST_INVALID_PHONE);
		user.setFirstName("anyName");
		user.setUsername("username");
		user.setLastName("lastName");
		
		MappingJacksonValue result = tweetUtil.filterUserData(Arrays.asList(user));
		assertNotNull(result);
	}

}
