package com.tweetapp.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tweetapp.document.TweetDoc;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TweetEventProducer {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendTweetEvent(TweetDoc tweet) throws JsonProcessingException {

		//filter
		FilterProvider filters = new SimpleFilterProvider().addFilter("TweetDocFilter", SimpleBeanPropertyFilter
				.filterOutAllExcept("message", "createdAt", "handle", "avatarUrl", "likesOnTweet", "reply"));

		ObjectWriter writer = new ObjectMapper().writer(filters);

		String key = tweet.getId();
		String value = writer.writeValueAsString(tweet);

		ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {
				log.error("tweet sent to kafka topic successfully");
			}

			@Override
			public void onFailure(Throwable ex) {
				log.info("sending tweet to kafka failed");

			}

		});
	}
}
