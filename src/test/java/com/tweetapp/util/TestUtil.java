package com.tweetapp.util;

import static com.tweetapp.constants.TweetConstants.BASE_PATH;
import static com.tweetapp.constants.TweetConstants.REQUEST_TYPE;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.constants.TweetConstants;
import com.tweetapp.constants.TweetConstants.REQUEST_TYPE;
import com.tweetapp.document.UserDoc;

@Component
public class TestUtil {
	
	/**
	 * method to return userdoc object from json file
	 * @param filePath
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public UserDoc getUserObjectFromJson(String filePath)
			throws JsonMappingException, JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(FileUtils.readFileToString(
				new File(TweetConstants.BASE_PATH + filePath).getAbsoluteFile(), "UTF-8"), UserDoc.class);

	}
	
	/**
	 * method to get the request from the resources
	 * @param requestType
	 * @return
	 * @throws IOException
	 */
	public String getLoginRequest(REQUEST_TYPE requestType) throws IOException {

		if (REQUEST_TYPE.GET_VALID_REQUEST == requestType) {
			return FileUtils.readFileToString(new File(BASE_PATH + "validLoginRequest.json").getAbsoluteFile(),
					"UTF-8");
		}

		return FileUtils.readFileToString(new File(BASE_PATH + "invalidLoginRequest.json").getAbsoluteFile(), "UTF-8");

	}
	
	/**
	 * method to get request as string format before calling the rest api for
	 * register user
	 * 
	 * @param requestType
	 * @return
	 * @throws IOException
	 */
	public String getRegisterUserRequest(REQUEST_TYPE requestType) throws IOException {
		if (REQUEST_TYPE.GET_VALID_REQUEST == requestType) {
			return FileUtils.readFileToString(new File(BASE_PATH + "validRegisterRequest.json").getAbsoluteFile(),
					"UTF-8");
		}

		return FileUtils.readFileToString(new File(BASE_PATH + "invalidPhoneRegisterRequest.json").getAbsoluteFile(),
				"UTF-8");

	}
}
