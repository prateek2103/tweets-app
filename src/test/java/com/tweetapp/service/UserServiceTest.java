package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.tweetapp.auth.jwt.JwtUtil;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.model.UserToken;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.service.impl.UserServiceImpl;
import com.tweetapp.util.TweetUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private TweetUtil tweetUtil;

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private IUserRepository userRepo;

	private UserDoc testUser;

	private static final String TEST_USER = "testUser";
	private static final String TEST_PASS = "test@123";
	private static final String TEST_TOKEN = "token123";

	@BeforeEach
	public void setup() {
		testUser = new UserDoc();
		testUser.setUsername(TEST_USER);
		testUser.setPassword(TEST_PASS);
	}

	@Test
	void test_loginUser() throws InvalidUserException {

		// when
		when(userRepo.findByUsername(any(String.class))).thenReturn(testUser);
		when(tweetUtil.comparePasswords(any(String.class), any(String.class))).thenReturn(true);
		when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(TEST_TOKEN);

		UserToken token = userService.loginUser(testUser);

		// then
		assertNotNull(token);
		assertEquals(TEST_USER, token.getUsername());
		assertEquals(TEST_TOKEN, token.getAuthToken());
	}

	@Test
	void test_loginUserThrowsException() {
		// when
		when(userRepo.findByUsername(any(String.class))).thenReturn(testUser);
		when(tweetUtil.comparePasswords(any(String.class), any(String.class))).thenReturn(false);

		// then
		InvalidUserException exception = assertThrows(InvalidUserException.class,
				() -> userService.loginUser(testUser));
		assertEquals(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG, exception.getMessage());
	}

	@Test
	void test_registerUserCallsRepo() throws InvalidUserException {
		// when
		when(tweetUtil.validateUserDetails(testUser)).thenReturn(true);
		when(tweetUtil.encryptPassword(TEST_PASS)).thenReturn(TEST_PASS);
		
		userService.registerUser(testUser);

		// then
		verify(userRepo, times(1)).save(testUser);
	}

	@Test
	void test_registerUserThrowsException() throws InvalidUserException {
		// when
		when(tweetUtil.validateUserDetails(testUser)).thenReturn(false);

		// then
		InvalidUserException exception = assertThrows(InvalidUserException.class,
				() -> userService.registerUser(testUser));
		assertEquals(TweetConstants.INVALID_USER_DETAILS, exception.getMessage());
	}

	@Test
	void test_forgetPasswordUserCallsRepo() throws InvalidTokenException {

		// when
		when(tweetUtil.getPureToken(TEST_TOKEN)).thenReturn(TEST_TOKEN);
		when(jwtUtil.validateToken(TEST_TOKEN)).thenReturn(true);
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(userRepo.findByUsername(TEST_USER)).thenReturn(testUser);
		when(tweetUtil.encryptPassword(TEST_PASS)).thenReturn(TEST_PASS);
		
		userService.forgetPasswordUser(TEST_USER, TEST_PASS, TEST_TOKEN);

		// then
		verify(userRepo, times(1)).save(testUser);

	}

	@Test
	void test_forgetPasswordUserThrowsException() throws InvalidTokenException {

		// when
		when(tweetUtil.getPureToken(TEST_TOKEN)).thenReturn(TEST_TOKEN);
		when(jwtUtil.validateToken(TEST_TOKEN)).thenReturn(false);

		// then
		assertThrows(InvalidTokenException.class,
				() -> userService.forgetPasswordUser(TEST_USER, TEST_PASS, TEST_TOKEN));

	}

}
