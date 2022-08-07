package com.tweetapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.model.AuthResponse;

@ExtendWith(MockitoExtension.class)
class TweetUtilTest {

	@InjectMocks
	private TweetUtil tweetUtil;
	
	@Mock
	private JwtUtil jwtUtil;

	private static final Long TEST_VALID_PHONE = 1234567890L;
	private static final Long TEST_INVALID_PHONE = 1234L;
	private static final String COMPLETE_TEST_VALID_TOKEN = "Bearer testToken";
	private static final String PURE_TEST_TOKEN = "testToken";
	private static final String COMPLETE_TEST_INVALID_TOKEN = "testToken";
	private static final String TEST_PASS = "testPass";
	private static final String TEST_USER = "testUser";
	private static final String TEST_ENCODED_PASS = "$2a$10$C59XTmX3vEXkGpuYcFmlLOf3pJZ109GQk4hiCooDl8WLp09h0AslO";

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
	void test_getPureToken() throws InvalidTokenException {
		String actualResult = tweetUtil.getPureToken(COMPLETE_TEST_VALID_TOKEN);

		// then
		assertEquals(PURE_TEST_TOKEN, actualResult);

	}

	@Test
	void test_getPureTokenThrowsException() throws InvalidTokenException {

		// then
		assertThrows(InvalidTokenException.class, () -> tweetUtil.getPureToken(COMPLETE_TEST_INVALID_TOKEN));

	}

	@Test
	void test_comparePasswords() {
		assertTrue(tweetUtil.comparePasswords(TEST_ENCODED_PASS, TEST_PASS));
	}
	
	@Test
	void test_getValidity() throws InvalidTokenException {
		when(jwtUtil.validateToken(PURE_TEST_TOKEN)).thenReturn(true);
		when(jwtUtil.extractUsername(PURE_TEST_TOKEN)).thenReturn(TEST_USER);
		
		//then
		AuthResponse authResponse = tweetUtil.getValidity(COMPLETE_TEST_VALID_TOKEN);
		assertEquals(TEST_USER,authResponse.getUsername());
		assertTrue(authResponse.isValid());
		
	}
	
	@Test
	void test_getValidityInvalidToken() throws InvalidTokenException {
		when(jwtUtil.validateToken(PURE_TEST_TOKEN)).thenReturn(false);
		
		//then
		AuthResponse authResponse = tweetUtil.getValidity(COMPLETE_TEST_VALID_TOKEN);
		assertFalse(authResponse.isValid());
		
	}

}
