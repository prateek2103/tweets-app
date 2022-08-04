package com.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * model to store user token
 * @author prateekpurohit
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {

	private String username;
	private String authToken;
}
