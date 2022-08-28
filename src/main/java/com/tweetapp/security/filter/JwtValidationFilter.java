package com.tweetapp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.util.JwtUtil;

/**
 * validation filter before each authenticated request
 * 
 * @author prateekpurohit
 *
 */
@Component
public class JwtValidationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// retrieve the token
		String jwtTokenString = request.getHeader(TweetConstants.JWT_HEADER);

		if (null != jwtTokenString) {
			try {
				jwtUtil.isValidToken(jwtTokenString);
			} catch (Exception e) {
				throw new BadCredentialsException(TweetConstants.INVALID_TOKEN_MSG);
			}
		} else {
			throw new BadCredentialsException(TweetConstants.TOKEN_NOT_PASSED_MSG);
		}

		filterChain.doFilter(request, response);
	}

	// avoid this filter for login rest api call
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getRequestURI().equals("/api/v1.0/tweets/login")
				|| request.getRequestURI().equals("/api/v1.0/tweets/register")
				|| request.getRequestURI().startsWith("/api/v1.0/tweets/swagger-ui")
				|| request.getRequestURI().startsWith("/api/v1.0/tweets/v3/api-docs")
				|| request.getRequestURI().equals("/login")
				|| request.getRequestURI().equals("/register");
	}
}
