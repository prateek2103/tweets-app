package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.document.UserDoc;

/**
 * repository to perform db operations on user
 * @author prateekpurohit
 *
 */
@Repository
public interface IUserRepository extends MongoRepository<UserDoc, String>  {

	/**
	 * method to get user by username
	 * @param username
	 * @return
	 */
	public UserDoc findByUsername(String username);

	/**
	 * method to get users by username( partial username)
	 * @param username
	 * @return
	 */
	public List<UserDoc> findByUsernameLike(String username);
}
