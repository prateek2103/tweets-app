package com.tweetapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * kafka consumer configuration
 * @author prateekpurohit
 *
 */
@Configuration
@EnableKafka
public class TweetEventsConsumerConfig {

}
