package com.tweetapp.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.document.UserDoc;
import com.tweetapp.model.UserToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * utility class to handle JWT related operations
 * 
 * @author prateekpurohit
 *
 */
@Component
public class JwtUtil {

	private static final SecretKey SECRET_KEY = Keys
			.hmacShaKeyFor(TweetConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

	/**
	 * method to create jwt token
	 * 
	 * @param user
	 * @return
	 */
	public UserToken createToken(UserDoc user) {

		// create token
		String jwtToken = Jwts.builder().claim(TweetConstants.USERNAME_CLAIM, user.getUsername())
				.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + 5*60000)).signWith(SECRET_KEY)
				.compact();

		return new UserToken(user.getUsername(), user.getAvatarUrl(), jwtToken);
	}

	/**
	 * method to validate the jwt token
	 * 
	 * @param token
	 * @return
	 */
	public boolean isValidToken(String token) {
		String pureTokeString = token.substring(6);
		Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(pureTokeString).getBody();
		return true;
	}

	/**
	 * method to extract username from the jwt token
	 * @param token
	 * @return
	 */
	public String extractUsername(String token) {
		String pureTokenString = token.substring(6);
		return (String)Jwts.parserBuilder()
				   .setSigningKey(SECRET_KEY)
				   .build()
				   .parseClaimsJws(pureTokenString)
				   .getBody()
				   .get(TweetConstants.USERNAME_CLAIM);
	}
}
