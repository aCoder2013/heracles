package com.song.heracles.client.exception;

/**
 * @author song
 */
public class HeraclesClientException extends Exception{

	public HeraclesClientException(String message) {
		super(message);
	}

	public HeraclesClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public HeraclesClientException(Throwable cause) {
		super(cause);
	}

	public HeraclesClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
