package com.tweetapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.document.UserDoc;

/**
 * repository to perform db operations on user
 * @author prateekpurohit
 *
 */
@Repository
public interface IUserRepository extends MongoRepository<UserDoc, String>  {
	public UserDoc findByUsername(String username);
}
