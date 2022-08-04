package com.tweetapp.exception;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tweetapp.constants.TweetConstants;
import com.tweetapp.model.TweetsExceptionHandlerModel;
import com.tweetapp.util.TweetUtil;

/**
 * global exception handler for the application
 * 
 * @author prateekpurohit
 *
 */
@ControllerAdvice
public class TweetsExceptionHandler {

	@Autowired
	private TweetsExceptionHandlerModel globalExceptionModel;

	@Autowired
	private TweetUtil tweetUtil;

	/**
	 * exception handler for NoTweetsFoundException.class
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = NoTweetsFoundException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> noTweetsFoundExceptionHandler(NoTweetsFoundException ex) {

		globalExceptionModel.setErrorMessage(ex.getMessage());
		globalExceptionModel.setErrorCreationDate(new Date());

		return new ResponseEntity<>(globalExceptionModel, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> handleUnauthorizedExceptions(UnauthorizedException ex) {
		globalExceptionModel.setErrorMessage(ex.getMessage());
		globalExceptionModel.setErrorCreationDate(new Date());

		return new ResponseEntity<>(globalExceptionModel, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Exception handler for duplicate field exception
	 * @param e
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> handleDuplicateException(DuplicateKeyException e) {

		String errMessage = String.format(TweetConstants.DUPE_KEY_MSG, tweetUtil.extractDupeFieldFromErrMsg(e.getMessage()));
		globalExceptionModel.setErrorMessage(errMessage);
		globalExceptionModel.setErrorDescription("");
		globalExceptionModel.setErrorCreationDate(new Date());
		
		return new ResponseEntity<>(globalExceptionModel, HttpStatus.BAD_REQUEST);
		
	}
	
	/**
	 * exception handler for HttpMessageNotReadableException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		globalExceptionModel.setErrorMessage(TweetConstants.JSON_PARSE_ERR_MSG);
		globalExceptionModel.setErrorDescription("");
		globalExceptionModel.setErrorCreationDate(new Date());
		
		return new ResponseEntity<>(globalExceptionModel, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * exception handler for InvalidUserException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> handleInvalidUserException(InvalidUserException e) {
		globalExceptionModel.setErrorMessage(e.getMessage());
		globalExceptionModel.setErrorDescription("");
		globalExceptionModel.setErrorCreationDate(new Date());
		
		return new ResponseEntity<>(globalExceptionModel, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * exception handler for InvalidTokenException
	 * @param e
	 * @return
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<TweetsExceptionHandlerModel> handleInvalidTokenException(InvalidTokenException e) {
		globalExceptionModel.setErrorMessage(TweetConstants.INVALID_TOKEN_MSG);
		globalExceptionModel.setErrorDescription("");
		globalExceptionModel.setErrorCreationDate(new Date());
		
		return new ResponseEntity<>(globalExceptionModel, HttpStatus.UNAUTHORIZED);
	}
}
