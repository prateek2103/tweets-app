package com.tweetapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.constants.TweetConstants.REQUEST_TYPE;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.model.LoginResponse;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.util.TestUtil;
import com.tweetapp.util.TweetUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * task-2
 * test cases for tweets controller
 * @author prateekpurohit
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class TweetControllerTest {

	@Autowired
	private ITweetRepository tweetRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestUtil testUtil;

	@Autowired
	private TweetUtil tweetUtil;

	@Autowired
	private IUserRepository userRepo;

	private UserDoc testUser;

	private ObjectMapper objectMapper;

	private static final String USER_REQUEST_JSON = "userRequest.json";

	private static final String TEST_TOKEN = "Bearer testToken";

	@BeforeEach
	public void setup() throws JsonProcessingException, IOException {
		log.info("setting up dummy data");

		// create a test user
		testUser = testUtil.getUserObjectFromJson(USER_REQUEST_JSON);

		// save the dummy data in the database (encrypt password beforehand)
		testUser.setPassword(tweetUtil.encryptPassword(testUser.getPassword()));
		userRepo.save(testUser);

		objectMapper = new ObjectMapper();

		log.info("dummy data setup successfully");
	}

	/**
	 * method to test getAllTweets
	 * 
	 * @throws Exception
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	@Test
	void test_getAllTweets() throws UnsupportedEncodingException, IOException, Exception {

		// get the auth token
		String response = mockMvc
				.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
						.content(testUtil.getLoginRequest(REQUEST_TYPE.GET_VALID_REQUEST)))
				.andReturn().getResponse().getContentAsString();

		LoginResponse userData = objectMapper.readValue(response, LoginResponse.class);

		String fullToken = "Bearer " + userData.getAuthToken();

		// send the api request
		String Response = mockMvc
				.perform(get("/api/v1.0/tweets/all").header("Authorization", fullToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// map the response
		TweetDoc[] actualResult = objectMapper.readValue(Response, TweetDoc[].class);
		List<TweetDoc> expectedResult = tweetRepository.findAll();

		// then
		assertEquals(expectedResult.size(), actualResult.length);
	}

	/**
	 * method to test getAllTweets throws exception
	 * 
	 * @throws InvalidTokenException
	 */
	@Test
	void test_getAllTweetsThrowsException() throws UnsupportedEncodingException, IOException, Exception {

		// send the api request
		mockMvc.perform(
				get("/api/v1.0/tweets/all").header("Authorization", TEST_TOKEN).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.INVALID_TOKEN_MSG))).andReturn();				

	}

	@AfterEach
	public void tearDown() {
		log.info("cleaning dummy data");

		// clean the test user
		UserDoc user = userRepo.findByUsername(testUser.getUsername());
		userRepo.delete(user);

		log.info("dummy data cleanup successful");
	}
}
