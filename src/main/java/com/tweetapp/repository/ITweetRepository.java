package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.document.Tweet;

/**
 * repository to handle crud operations on tweet
 * @author prateekpurohit
 *
 */
@Repository
public interface ITweetRepository extends MongoRepository<Tweet, String> {
	
	/**
	 * method to retrieve all tweets by a username aka handle
	 * @param handle
	 * @return
	 */
	public List<Tweet> findByHandle(String handle);
}
