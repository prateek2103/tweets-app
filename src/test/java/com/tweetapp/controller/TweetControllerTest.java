package com.tweetapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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
import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.util.TestUtil;
import com.tweetapp.util.TweetUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * task-2 test cases for tweets controller
 * 
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

	private static final String TEST_USER = "testUser";

	private static final String TEST_USER_NOT_EXISTS = "testUserNotExists";

	@BeforeEach
	public void setup() throws JsonProcessingException, IOException {
		log.info("setting up dummy data");

		// create a test user
		testUser = testUtil.getUserObjectFromJson(USER_REQUEST_JSON);

		// save the dummy data in the database (encrypt password beforehand)
		testUser.setPassword(tweetUtil.encryptPassword(testUser.getPassword()));
		userRepo.save(testUser);

		objectMapper = new ObjectMapper();

		// create a new tweet
		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER);
		tweet.setAvatarUrl("url");
		tweet.setCreatedAt(new Date());
		tweet.setMessage("message");
		tweet.setReply(true);

		// save in the database
		tweetRepository.save(tweet);

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

		String fullToken = testUtil.getAuthToken();

		// send the api request
		String resp = mockMvc
				.perform(get("/api/v1.0/tweets/all").header("Authorization", fullToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// map the response
		TweetDoc[] actualResult = objectMapper.readValue(resp, TweetDoc[].class);
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

	/**
	 * method toe test getTweetsBysUsername rest api call
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_getTweetsByUsername() throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		// send the api request
		String resp = mockMvc
				.perform(get("/api/v1.0/tweets/" + TEST_USER).header("Authorization", fullToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		TweetDoc[] actualResult = objectMapper.readValue(resp, TweetDoc[].class);
		List<TweetDoc> expectedResult = tweetRepository.findByHandle(TEST_USER);

		// then
		assertEquals(expectedResult.size(), actualResult.length);
	}

	/**
	 * method to test getTweetsByUsername throws exception on no tweets
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_getTweetsByUsernameThrowsExceptionOnNoTweets()
			throws UnsupportedEncodingException, IOException, Exception {
		String fullToken = testUtil.getAuthToken();

		// send the api request
		mockMvc.perform(get("/api/v1.0/tweets/" + TEST_USER_NOT_EXISTS).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEETS_NOT_FOUND_MESSAGE)));

	}

	/**
	 * method to test getTweetsByUsername throws exception on invalid token
	 * 
	 * @throws Exception
	 */
	@Test
	void test_getTweetsByUsernameThrowsExceptionOnInvalidToken() throws Exception {
		// send the api request
		mockMvc.perform(get("/api/v1.0/tweets/" + TEST_USER_NOT_EXISTS).header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTokenException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.INVALID_TOKEN_MSG)));
	}

	@AfterEach
	public void tearDown() {
		log.info("cleaning dummy data");

		// clean the test user
		UserDoc user = userRepo.findByUsername(testUser.getUsername());
		userRepo.delete(user);

		// clean the test tweets
		tweetRepository.deleteByHandle(TEST_USER);

		log.info("dummy data cleanup successful");
	}

}
