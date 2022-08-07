package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.document.TweetDoc;

/**
 * repository to handle crud operations on tweet
 * @author prateekpurohit
 *
 */
@Repository
public interface ITweetRepository extends MongoRepository<TweetDoc, String> {
	
	/**
	 * method to retrieve all tweets by a username aka handle
	 * @param handle
	 * @return
	 */
	public List<TweetDoc> findByHandle(String handle);

	public void deleteByHandle(String handle);
}
