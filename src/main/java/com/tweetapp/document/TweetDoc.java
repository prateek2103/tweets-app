package com.tweetapp.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.mongodb.lang.NonNull;

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
@Document(collection="tweets")
@JsonFilter("TweetDocFilter")
public class TweetDoc {
	@Id
	private String id;
	
	@Field(name="msg")
	@NonNull
	private String message;
	
	@Field(name="creat_tm")
	private Date createdAt;
	
	@NonNull
	private String handle;
	
	private String avatarUrl;
	
	@Field(name="likes")
	private Long likesOnTweet = 0L;
	
	private boolean isReply = false;
	
	@DBRef(lazy = true)
	private List<TweetDoc> replies;
}
