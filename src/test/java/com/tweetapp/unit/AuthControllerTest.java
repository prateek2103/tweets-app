package com.tweetapp.unit;

import static com.tweetapp.constants.TweetConstants.BASE_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.model.LoginResponse;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.util.TestUtil;
import com.tweetapp.util.TweetUtil;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private UserDoc testUser;

	private UserDoc testRegisterUser;
	
	private enum REQUEST_TYPE {
		GET_VALID_REQUEST, GET_INVALID_REQUEST
	}

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
	private TweetUtil tweetUtil;

	@BeforeEach
	public void setup() throws JsonProcessingException, IOException {
		log.info("setting up dummy data");

		// create a test user
		testUser = testUtil.getUserObjectFromJson(USER_REQUEST_JSON);
		
		//create a test user for register
		testRegisterUser  = testUtil.getUserObjectFromJson(USER_REGISTER_REQUEST_JSON);

		// save the dummy data in the database (encrypt password beforehand)
		testUser.setPassword(tweetUtil.encryptPassword(testUser.getPassword()));
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
				.content(getLoginRequest(REQUEST_TYPE.GET_VALID_REQUEST))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(testUser.getUsername()))).andReturn();

	}

	/**
	 * method to test login for invalid login credentials
	 * @throws Exception
	 */
	@Test
	void test_login_invalidRequest() throws Exception {

		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content(getLoginRequest(REQUEST_TYPE.GET_INVALID_REQUEST))).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidUserException))
				.andExpect(result -> assertEquals(
						result.getResolvedException().getMessage(),TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG))
				.andReturn();

	}

	/**
	 * method to test forget password for valid user
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_forgetPasswordValidUser() throws IOException, Exception {
		String uri = String.format("/%s/forgetPassword", testUser.getUsername());

		//get the token via rest api call to login
		String response = mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
						.content(getLoginRequest(REQUEST_TYPE.GET_VALID_REQUEST)))
				.andReturn().getResponse().getContentAsString();

		//convert string response to json to get the token
		ObjectMapper objectMapper = new ObjectMapper();
		LoginResponse userData = objectMapper.readValue(response, LoginResponse.class);

		// call the rest api
		String updateResponse = mockMvc
				.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(TEST_PASSWORD).header(AUTH_HEADER,
						"Bearer " + userData.getAuthToken()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// assert the response
		assertEquals(TweetConstants.UPDATE_PASS_MSG, updateResponse);

		// check if user's password is updated in the database
		UserDoc responseUser = userRepo.findByUsername(testUser.getUsername());
		assertTrue(tweetUtil.comparePasswords(responseUser.getPassword(), TEST_PASSWORD));
	}

	/**
	 * method to test forget password for invalid token
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_forgetPasswordInvalidToken() throws IOException, Exception {
		String uri = String.format("/%s/forgetPassword", testUser.getUsername());

		// call the rest api
		mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(TEST_PASSWORD).header(AUTH_HEADER,
				TEST_TOKEN)).andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
				.andExpect(jsonPath("$.errorMessage",is(TweetConstants.INVALID_TOKEN_MSG)))
				.andReturn().getResponse().getContentAsString();

		// check if user's password is updated in the database
		UserDoc responseUser = userRepo.findByUsername(testUser.getUsername());
		assertFalse(tweetUtil.comparePasswords(responseUser.getPassword(), TEST_PASSWORD));
	}

	/**
	 * test register api call with valid request
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_registerUserValidRequest() throws UnsupportedEncodingException, Exception {
		mockMvc.perform
			(post("/register").
				contentType(MediaType.APPLICATION_JSON)
				.content(getRegisterUserRequest(REQUEST_TYPE.GET_VALID_REQUEST)))
				.andExpect(status().isCreated())
				.andReturn();
		
		//check if the user is saved in the database
		UserDoc actualUser = userRepo.findByUsername(testRegisterUser.getUsername());
		assertNotNull(actualUser);
	}
	
	/**
	 * test register user invalid register request (phone number is 8 characters long)
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	void test_registerUserInvalidRequest() throws UnsupportedEncodingException, Exception {
		mockMvc.perform
			(post("/register").
				contentType(MediaType.APPLICATION_JSON)
				.content(getRegisterUserRequest(REQUEST_TYPE.GET_INVALID_REQUEST)))
				.andExpect(status().isBadRequest())
				.andExpect(result->assertTrue(result.getResolvedException() instanceof InvalidUserException))
				.andExpect(result->assertEquals(result.getResolvedException().getMessage(),TweetConstants.INVALID_PHONE_NUM_MSG))
				.andReturn();
		
		//check if the user is saved in the database
		UserDoc actualUser = userRepo.findByUsername(testRegisterUser.getUsername());
		assertNull(actualUser);
	}
	@AfterEach
	public void tearDown() {

		log.info("cleaning dummy data");
		//clean the test user
		UserDoc user = userRepo.findByUsername(testUser.getUsername());
		userRepo.delete(user);
		
		//clean the test user for registration
		UserDoc registerUser= userRepo.findByUsername(testRegisterUser.getUsername());
		
		if(registerUser!=null)
			userRepo.delete(registerUser);
		
		log.info("dummy data cleanup successful");
	}

	/**
	 * get login request for the rest api call
	 * 
	 * @param getValidRequest
	 * @return
	 * @throws IOException
	 */
	private String getLoginRequest(REQUEST_TYPE requestType) throws IOException {

		if (REQUEST_TYPE.GET_VALID_REQUEST == requestType) {
			return FileUtils.readFileToString(new File(BASE_PATH + "validLoginRequest.json").getAbsoluteFile(),
					"UTF-8");
		}

		return FileUtils.readFileToString(new File(BASE_PATH + "invalidLoginRequest.json").getAbsoluteFile(), "UTF-8");

	}
	
	
	/**
	 * method to get request as string format before calling the rest api for register user
	 * @param requestType
	 * @return
	 * @throws IOException
	 */
	private String getRegisterUserRequest(REQUEST_TYPE requestType) throws IOException {
		if (REQUEST_TYPE.GET_VALID_REQUEST == requestType) {
			return FileUtils.readFileToString(new File(BASE_PATH + "validRegisterRequest.json").getAbsoluteFile(),
					"UTF-8");
		}

		return FileUtils.readFileToString(new File(BASE_PATH + "invalidPhoneRegisterRequest.json").getAbsoluteFile(), "UTF-8");
		
	}

}
