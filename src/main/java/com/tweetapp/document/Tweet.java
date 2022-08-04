package com.tweetapp.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * tweet model for storing and retrieving tweets
 * @author prateekpurohit
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection="tweet")
public class Tweet {
	@Id
	private String id;
	
	@Field(name="msg")
	private String message;
	
	@Field(name="creat_tm")
	private Date createdAt;
	
	private String handle;
	
	private String name;
	
	private String avatarUrl;
	
	@Field(name="likes")
	private Long likesOnTweet;
	
	private boolean isReply;
	
	@DBRef
	private List<Tweet> replies;
}
