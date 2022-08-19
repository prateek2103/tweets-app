package com.tweetapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.constants.TweetConstants.REQUEST_TYPE;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoUsersFoundException;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.util.TestUtil;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private UserDoc testUser;

	private UserDoc testRegisterUser;

	private ObjectMapper objectMapper = new ObjectMapper();

	private static final String USER_REQUEST_JSON = "userRequest.json";
	private static final String USER_REGISTER_REQUEST_JSON = "validRegisterRequest.json";
	private static final String AUTH_HEADER = "Authorization";
	private static final String TEST_PASSWORD = "newPasswordForTestUser";
	private static final String TEST_TOKEN = "testtoken";

	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private TestUtil testUtil;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setup() throws JsonProcessingException, IOException {
		log.info("setting up dummy data");

		// create a test user
		testUser = testUtil.getUserObjectFromJson(USER_REQUEST_JSON);

		// create a test user for register
		testRegisterUser = testUtil.getUserObjectFromJson(USER_REGISTER_REQUEST_JSON);

		// save the dummy data in the database (encrypt password beforehand)
		testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
		userRepo.save(testUser);

		log.info("dummy data setup successfully");
	}

	/**
	 * test the login rest call
	 * 
	 * @throws Exception
	 */
	@Test
	void test_login_validRequest() throws Exception {

		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.getLoginRequest(REQUEST_TYPE.GET_VALID_REQUEST))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(testUser.getUsername()))).andReturn();

	}

	/**
	 * method to test login for invalid login credentials
	 * 
	 * @throws Exception
	 */
	@Test
	void test_login_invalidRequest() throws Exception {

		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.getLoginRequest(REQUEST_TYPE.GET_INVALID_REQUEST)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
				.andExpect(result -> assertEquals(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG,
						result.getResolvedException().getMessage()))
				.andReturn();

	}

	/**
	 * method to test forget password for valid user
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_forgetPasswordValidUser() throws IOException, Exception {
		String uri = String.format("/%s/forgetPassword", testUser.getUsername());

		String tokenString = testUtil.getAuthToken();

		// call the rest api
		String updateResponse = mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(TEST_PASSWORD)
				.header(AUTH_HEADER, tokenString)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();

		// assert the response
		assertEquals(TweetConstants.UPDATE_PASS_MSG, updateResponse);

		// check if user's password is updated in the database
		UserDoc responseUser = userRepo.findByUsername(testUser.getUsername());
		assertTrue(passwordEncoder.matches(TEST_PASSWORD, responseUser.getPassword()));
	}

	/**
	 * test method forget password when token is invalid
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_forgetPasswordInvalidToken() throws IOException, Exception {
		String uri = String.format("/%s/forgetPassword", testUser.getUsername());

		// call the rest api
		mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(TEST_PASSWORD).header(AUTH_HEADER,
				TEST_TOKEN)).andExpect(status().isUnauthorized());

		// check if user's password is updated in the database
		UserDoc responseUser = userRepo.findByUsername(testUser.getUsername());
		assertFalse(passwordEncoder.matches(TEST_PASSWORD, responseUser.getPassword()));
	}

	/**
	 * test register api call with valid request
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_registerUserValidRequest() throws UnsupportedEncodingException, Exception {
		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.getRegisterUserRequest(REQUEST_TYPE.GET_VALID_REQUEST)))
				.andExpect(status().isCreated()).andReturn();

		// check if the user is saved in the database
		UserDoc actualUser = userRepo.findByUsername(testRegisterUser.getUsername());
		assertNotNull(actualUser);
	}

	/**
	 * test register user invalid register request (phone number is 8 characters
	 * long)
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_registerUserInvalidRequest() throws UnsupportedEncodingException, Exception {
		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
				.content(testUtil.getRegisterUserRequest(REQUEST_TYPE.GET_INVALID_REQUEST)))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidUserException))
				.andExpect(result -> assertEquals(TweetConstants.INVALID_PHONE_NUM_MSG,
						result.getResolvedException().getMessage()))
				.andReturn();

		// check if the user is saved in the database
		UserDoc actualUser = userRepo.findByUsername(testRegisterUser.getUsername());
		assertNull(actualUser);
	}

	/**
	 * task-1 method to test to retrieve all users
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_getAllUsers() throws UnsupportedEncodingException, Exception {
		String tokenString = testUtil.getAuthToken();

		String response = mockMvc.perform(get("/users/all").header(AUTH_HEADER, tokenString)).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		// convert response to UserDoc list
		UserDoc[] actualUsers = objectMapper.readValue(response, UserDoc[].class);
		List<UserDoc> expectedUsers = userRepo.findAll();

		// assert
		assertEquals(expectedUsers.size(), actualUsers.length);
	}

	/**
	 * test method get all users throws exception on invalid user
	 * 
	 * @throws Exception
	 */
	@Test
	void test_getAllUsersThrowsExceptionOnInvalidToken() throws Exception {
		mockMvc.perform(get("/users/all").header(AUTH_HEADER, TEST_TOKEN)).andExpect(status().isUnauthorized())
				.andReturn();
	}

	/**
	 * method to test get users by username when users exists
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_getUsersByUsername_userExists() throws UnsupportedEncodingException, Exception {
		String partialUsername = "us";
		String tokenString = testUtil.getAuthToken();

		String response = mockMvc.perform(get("/user/search/" + partialUsername).header(AUTH_HEADER, tokenString))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// convert response to UserDoc list
		UserDoc[] actualUsers = objectMapper.readValue(response, UserDoc[].class);
		List<UserDoc> expectedUsers = userRepo.findByUsernameLike("*" + partialUsername + "*");

		// assert
		assertEquals(expectedUsers.size(), actualUsers.length);
	}

	/**
	 * method to test get users by username when users don't exists
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_getUsersByUsername_usersNotExists() throws UnsupportedEncodingException, Exception {
		String partialUsername = "testUserNotExist";
		String tokenString = testUtil.getAuthToken();

		mockMvc.perform(get("/user/search/" + partialUsername).header(AUTH_HEADER, tokenString))
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NoUsersFoundException))
				.andReturn();

	}

	/**
	 * test_getUsersByUsername throws exception on invalid token
	 * @throws Exception
	 */
	@Test
	void test_getUsersByUsernameThrowsExceptionOnInvalidToken() throws Exception {
		String partialUsername = "testUserNotExist";

		mockMvc.perform(get("/user/search/" + partialUsername).header(AUTH_HEADER, TEST_TOKEN))
				.andExpect(status().isUnauthorized());
	}

	@AfterEach
	public void tearDown() {

		log.info("cleaning dummy data");
		// clean the test user
		UserDoc user = userRepo.findByUsername(testUser.getUsername());
		userRepo.delete(user);

		// clean the test user for registration
		UserDoc registerUser = userRepo.findByUsername(testRegisterUser.getUsername());

		if (registerUser != null)
			userRepo.delete(registerUser);

		log.info("dummy data cleanup successful");
	}

}
