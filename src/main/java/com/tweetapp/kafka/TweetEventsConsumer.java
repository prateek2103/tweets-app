package com.tweetapp.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.document.TweetDoc;
import com.tweetapp.exception.InvalidTweetException;
import com.tweetapp.service.ITweetService;

@Component
public class TweetEventsConsumer {

	@Autowired
	private ITweetService tweetService;

	@KafkaListener(topics = { "tweet-events" })
	public void onMessage(ConsumerRecord<String, String> consumerRecord)
			throws JsonProcessingException, InvalidTweetException {
		String value = consumerRecord.value();
		
		//get the tweet object
		TweetDoc tweet = new ObjectMapper().readValue(value, TweetDoc.class);

		//save the tweet
		tweetService.addTweet(tweet);
	}
}
