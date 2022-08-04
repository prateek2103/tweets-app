package com.tweetapp.model;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * global exception model for global exception handler class
 * @author prateekpurohit
 *
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetsExceptionHandlerModel {

	private String errorMessage;
	private String errorDescription;
	private Date errorCreationDate;
}
