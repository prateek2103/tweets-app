package com.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * model for authentication response
 * 
 * @author prateekpurohit
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String username;
	private boolean isValid;
}
