package com.tweetapp.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.constants.TweetConstants;
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
}
