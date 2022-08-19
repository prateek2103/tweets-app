package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.model.UserToken;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.service.impl.UserServiceImpl;
import com.tweetapp.util.JwtUtil;
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

	@Mock
	private PasswordEncoder passwordEncoder;

	private UserDoc testUser;

	private static final String TEST_USER = "testUser";
	private static final String TEST_USER_2 = "testUser2";
	private static final String TEST_PASS = "test@123";
	private static final String TEST_TOKEN = "token123";

	@BeforeEach
	public void setup() {
		testUser = new UserDoc();
		testUser.setUsername(TEST_USER);
		testUser.setPassword(TEST_PASS);
	}

	/**
	 * method to test loginUser method
	 * 
	 * @throws InvalidUserException
	 */
	@Test
	void test_loginUser() throws InvalidUserException {

		// when
		when(userRepo.findByUsername(any(String.class))).thenReturn(testUser);
		when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
		when(jwtUtil.createToken(any(UserDoc.class))).thenReturn(new UserToken(TEST_USER, TEST_TOKEN));

		UserToken token = userService.loginUser(testUser);

		// then
		assertNotNull(token);
		assertEquals(TEST_USER, token.getUsername());
		assertEquals(TEST_TOKEN, token.getAuthToken());
	}

	/**
	 * test method loginUser throws exception on wrong password
	 */
	@Test
	void test_loginUserThrowsException() {
		// when
		when(userRepo.findByUsername(any(String.class))).thenReturn(testUser);
		when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

		// then
		BadCredentialsException exception = assertThrows(BadCredentialsException.class,
				() -> userService.loginUser(testUser));

		assertEquals(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG, exception.getMessage());
	}

	/**
	 * test method register user calls the repository
	 * 
	 * @throws InvalidUserException
	 */
	@Test
	void test_registerUserCallsRepo() throws InvalidUserException {
		// when
		when(tweetUtil.validateUserDetails(testUser)).thenReturn(true);
		when(passwordEncoder.encode(TEST_PASS)).thenReturn(TEST_PASS);

		userService.registerUser(testUser);

		// then
		verify(userRepo, times(1)).save(testUser);
	}

	/**
	 * test method registerUser to throw exception on invalid user details
	 * 
	 * @throws InvalidUserException
	 */
	@Test
	void test_registerUserThrowsException() throws InvalidUserException {
		// when
		when(tweetUtil.validateUserDetails(testUser)).thenReturn(false);

		// then
		InvalidUserException exception = assertThrows(InvalidUserException.class,
				() -> userService.registerUser(testUser));
		assertEquals(TweetConstants.INVALID_USER_DETAILS, exception.getMessage());
	}

	/**
	 * test method forgetPasswordUser calls the repository
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_forgetPasswordUserCallsRepo() throws InvalidTokenException {

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER);
		when(userRepo.findByUsername(TEST_USER)).thenReturn(testUser);
		when(passwordEncoder.encode(TEST_PASS)).thenReturn(TEST_PASS);

		userService.forgetPasswordUser(TEST_USER, TEST_PASS, TEST_TOKEN);

		// then
		verify(userRepo, times(1)).save(testUser);

	}

	/**
	 * test method forgetPasswordUser throws exception when user does not match with
	 * the token's user
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_forgetPasswordUserThrowsException() throws InvalidTokenException {

		// when
		when(jwtUtil.extractUsername(TEST_TOKEN)).thenReturn(TEST_USER_2);

		// then
		assertThrows(BadCredentialsException.class,
				() -> userService.forgetPasswordUser(TEST_USER, TEST_PASS, TEST_TOKEN));

	}

	/**
	 * test method get all users calls repository
	 * 
	 * @throws NoUsersFoundException
	 */
	@Test
	void test_getAllUsersCallsRepo() throws NoUsersFoundException {
		// when
		when(userRepo.findAll()).thenReturn(Arrays.asList(new UserDoc()));

		userService.getAllUsers();

		// then
		verify(userRepo, times(1)).findAll();
	}

	/**
	 * test method get all users throws exception
	 * 
	 * @throws NoUsersFoundException
	 */
	@Test
	void test_getAllUsersThrowsException() throws NoUsersFoundException {

		// when
		when(userRepo.findAll()).thenReturn(new ArrayList<>());

		// then
		assertThrows(NoUsersFoundException.class, () -> userService.getAllUsers());
	}

	/**
	 * test method getUsersByUsername calls repository
	 * @throws NoUsersFoundException
	 */
	@Test
	void test_getUsersByUsername() throws NoUsersFoundException {
		// when
		when(userRepo.findByUsernameLike("*" + TEST_USER + "*")).thenReturn(Arrays.asList(new UserDoc()));

		userService.getUsersByUsername(TEST_USER);

		// then
		verify(userRepo, times(1)).findByUsernameLike("*" + TEST_USER + "*");

	}

	/**
	 * test method getUsersByUsername throws exception on no users
	 * @throws NoUsersFoundException
	 */
	@Test
	void test_getUsersByUsernameThrowsException() throws NoUsersFoundException {
		// when
		when(userRepo.findByUsernameLike("*" + TEST_USER + "*")).thenReturn(new ArrayList<>());

		// then
		assertThrows(NoUsersFoundException.class, () -> userService.getUsersByUsername(TEST_USER));

	}

}
