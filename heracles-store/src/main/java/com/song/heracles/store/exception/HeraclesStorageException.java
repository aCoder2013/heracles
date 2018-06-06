package com.song.heracles.store.exception;

/**
 * @author song
 */
public class HeraclesStorageException extends Exception {

	private int code;

	public HeraclesStorageException(String message, int code) {
		super(message);
		this.code = code;
	}

	public HeraclesStorageException(String message, Throwable cause, int code) {
		super(message, cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
