package com.tweetapp.controller;

import static com.tweetapp.constants.TweetConstants.BASE_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
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
import com.tweetapp.document.TweetDoc;
import com.tweetapp.document.UserDoc;
import com.tweetapp.exception.InvalidTokenException;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.exception.InvalidUserException;
import com.tweetapp.exception.NoTweetsFoundException;
import com.tweetapp.repository.ITweetRepository;
import com.tweetapp.repository.IUserRepository;
import com.tweetapp.util.TestUtil;

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
	private IUserRepository userRepo;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	private UserDoc testUser;

	private ObjectMapper objectMapper;

	private static final String USER_REQUEST_JSON = "userRequest.json";

	private static final String TEST_TOKEN = "Bearer testToken";

	private static final String TEST_USER = "testUser";

	private static final String TEST_USER_2 = "testUser2";

	private static final String TEST_USER_NOT_EXISTS = "testUserNotExists";

	private String testId;
	private String testId2;

	@BeforeEach
	public void setup() throws JsonProcessingException, IOException {
		log.info("setting up dummy data");

		// create a test user
		testUser = testUtil.getUserObjectFromJson(USER_REQUEST_JSON);

		// save the dummy data in the database (encrypt password beforehand)
		testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
		userRepo.save(testUser);

		objectMapper = new ObjectMapper();

		// create a new tweet
		TweetDoc tweet = new TweetDoc();
		tweet.setHandle(TEST_USER);
		tweet.setAvatarUrl("url");
		tweet.setCreatedAt(new Date());
		tweet.setMessage("message");
		tweet.setReply(true);
		tweet.setLikesOnTweet(1L);
		tweet.setReplies(new ArrayList<>());

		// save in the database
		testId = tweetRepository.save(tweet).getId();
				
		//create tweet for reply api case
		TweetDoc tweet2 = new TweetDoc();
		tweet2.setHandle(TEST_USER_2);
		tweet2.setAvatarUrl("url");
		tweet2.setCreatedAt(new Date());
		tweet2.setMessage("message");
		tweet2.setReply(true);
		tweet2.setLikesOnTweet(1L);
		tweet2.setReplies(new ArrayList<>());
		
		// save in the database
		testId2 = tweetRepository.save(tweet2).getId();

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
				.perform(get("/tweets/all").header("Authorization", fullToken).contentType(MediaType.APPLICATION_JSON))
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
				.andExpect(status().isUnauthorized());
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
				.perform(get("/tweets/" + TEST_USER).header("Authorization", fullToken)
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
		mockMvc.perform(get("/tweets/" + TEST_USER_NOT_EXISTS).header("Authorization", fullToken)
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
		mockMvc.perform(get("/tweets/" + TEST_USER_NOT_EXISTS).header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}

	/**
	 * method to test delete tweet by id
	 * 
	 * @throws Exception
	 */
	@Test
	void test_deleteTweetById() throws Exception {
		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(delete("/tweets/" + TEST_USER + "/delete/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// assert
		Optional<TweetDoc> tweet = tweetRepository.findById(testId);
		assertFalse(tweet.isPresent());

	}

	/**
	 * method to test deleteTweetById throws exception when tweet does not exist
	 * 
	 * @throws Exception
	 */
	@Test
	void test_deleteTweetByIdThrowsExceptionTweetNotExists() throws Exception {
		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(delete("/tweets/" + TEST_USER + "/delete/" + 123).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NoTweetsFoundException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEET_NOT_EXIST_MSG)));
	}

	/**
	 * method to test deleteTweetById throws exception when username is differnent
	 * then the tweet's
	 * 
	 * @throws Exception
	 */
	@Test
	void test_deleteTweetByIdThrowsExceptionWhenTweetUserisDifferent() throws Exception {
		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(delete("/tweets/" + TEST_USER_2 + "/delete/" + testId)
				.header("Authorization", fullToken).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * method to test deleteTweetById throws exception when token is invalid
	 * 
	 * @throws Exception
	 */
	@Test
	void test_deleteTweetByIdThrowsExceptionOnInvalidToken() throws Exception {

		mockMvc.perform(delete("/tweets/" + TEST_USER + "/delete/" + testId)
				.header("Authorization", TEST_TOKEN).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * method to test likeTweetById
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_likeTweetById() throws UnsupportedEncodingException, IOException, Exception {
		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/like/" + testId2).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// assert
		Optional<TweetDoc> tweet = tweetRepository.findById(testId2);
		assertEquals(2, tweet.get().getLikesOnTweet());

	}

	/**
	 * method to test likeTweetsById throws exception on invalid token
	 * 
	 * @throws Exception
	 */
	@Test
	void test_likeTweetByIdOnInvalidToken() throws Exception {

		mockMvc.perform(post("/tweets/" + TEST_USER + "/like/" + testId).header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
		}

	/**
	 * method to test likeTweetById throws error when no tweet exists
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_likeTweetByIdThrowsExceptionWhenTweetNotExists()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/like/" + 123).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NoTweetsFoundException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEET_NOT_EXIST_MSG)));

	}

	/**
	 * test method getTweetsById throws error when tweet user is same as the tweet
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_likeTweetByIdThrowsExceptionWhenTweetUserIsSame()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/like/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidUserException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.USER_NOT_LIKE_MSG)));

	}

	/**
	 * test method replyTweetById tweet does not exist
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionWhenTweetNotExists()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/reply/" + 123).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NoTweetsFoundException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEET_NOT_EXIST_MSG)));

	}

	/**
	 * method to test replyTweetById throws exception when user is different from
	 * the token's
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionWhenInvalidUser()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER_2 + "/reply/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG)));

	}

	/**
	 * method to test replyTweetById throws exceptionw when invalid token is passed
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionWhenInvalidToken()
			throws UnsupportedEncodingException, IOException, Exception {

		mockMvc.perform(post("/tweets/" + TEST_USER + "/reply/" + 123).header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()));

	}

	/**
	 * method to test replyTweetById when message limit exceeds 144
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_replyTweetByIdThrowsExceptionWhenMessageExceeds()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/reply/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(invalidReplyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTweetException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEET_LIMIT_EXCEED)));

	}

	/**
	 * test method replyTweetById
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_replyTweetById() throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/reply/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet())).andExpect(status().isOk());

		// assert
		TweetDoc tweet = tweetRepository.findById(testId).get();
		assertEquals("this is a new tweet by newHandle", tweet.getReplies().get(0).getMessage());

	}

	/**
	 * test method to updateTweetById throws exception when message limit exceeds
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionWhenMessageExceeds()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(put("/tweets/" + TEST_USER + "/update/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(invalidReplyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTweetException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.TWEET_LIMIT_EXCEED)));

	}

	/**
	 * test method to updateTweetById throws excepton on invalid token
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionWhenInvalidToken()
			throws UnsupportedEncodingException, IOException, Exception {

		mockMvc.perform(put("/tweets/" + TEST_USER + "/update/" + 123).header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()));

	}

	/**
	 * test method updateTweetById throws exception on invalid user
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionWhenInvalidUser()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(put("/tweets/" + TEST_USER_2 + "/update/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG)));

	}

	/**
	 * test method updateTweetById throws exception when tweet user is different
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_updateTweetByIdThrowsExceptionWhenTweetUserIsDifferent()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(put("/tweets/" + TEST_USER + "/update/" + testId2).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidUserException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.INVALID_USER_DETAILS)));

	}

	/**
	 * method to test update tweet by id
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_updateTweetById() throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(put("/tweets/" + TEST_USER + "/update/" + testId).header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(updateTweet())).andExpect(status().isOk());

		// assert
		TweetDoc tweet = tweetRepository.findById(testId).get();
		assertEquals("new message for an old tweet", tweet.getMessage());
	}

	/**
	 * method to test post tweet
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_postTweet() throws IOException, Exception {
		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER + "/add").header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet())).andExpect(status().isCreated());

	}

	/**
	 * method to test post tweet throws error on invalid token
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_postTweetThrowsExceptionWhenInvalidToken() throws UnsupportedEncodingException, IOException, Exception {

		mockMvc.perform(post("/tweets/" + TEST_USER + "/add").header("Authorization", TEST_TOKEN)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()));

	}

	/**
	 * method to test post tweet throws error on invalid username
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	void test_postTweetThrowsExceptionWhenInvalidUsername()
			throws UnsupportedEncodingException, IOException, Exception {

		String fullToken = testUtil.getAuthToken();

		mockMvc.perform(post("/tweets/" + TEST_USER_2 + "/add").header("Authorization", fullToken)
				.contentType(MediaType.APPLICATION_JSON).content(replyTweet()))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
				.andExpect(jsonPath("$.errorMessage", is(TweetConstants.UNAUTHORIZED_USER_ACCESS_MSG)));

	}

	@AfterEach
	public void tearDown() {
		log.info("cleaning dummy data");

		// clean the test user
		UserDoc user = userRepo.findByUsername(testUser.getUsername());
		userRepo.delete(user);

		// clean the test tweets
		tweetRepository.deleteByHandle(TEST_USER);
		tweetRepository.deleteByHandle(TEST_USER_2);

		log.info("dummy data cleanup successful");
	}

	private String replyTweet() throws IOException {
		return FileUtils.readFileToString(new File(BASE_PATH + "tweetReplyValidRequest.json").getAbsoluteFile(),
				"UTF-8");

	}

	private String invalidReplyTweet() throws IOException {
		return FileUtils.readFileToString(new File(BASE_PATH + "tweetReplyInvalidRequest.json").getAbsoluteFile(),
				"UTF-8");

	}

	private String updateTweet() throws IOException {
		return FileUtils.readFileToString(new File(BASE_PATH + "updateTweetValid.json").getAbsoluteFile(), "UTF-8");

	}

}
