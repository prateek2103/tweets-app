package com.tweetapp.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * collection model to store user's information
 * email and username fields will be unique
 * @author prateekpurohit
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="users")
@JsonFilter("UserDocFilter")
public class UserDoc {
	@Id
	private String id;
	
	@NonNull
	private String firstName;
	
	private String lastName;
	
	private String avatarUrl;
	
	@NonNull
	@Indexed(unique=true)
	private String email;
	
	@NonNull
	@Indexed(unique=true)
	private String username;
	
	@NonNull
	private String password;
	
	@NonNull
	private Long contactNumber;
}
