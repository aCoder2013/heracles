package com.song.heracles.store.exception;

import com.song.heracles.common.constants.ErrorCode;

/**
 * @author song
 */
public class StreamNotAvailableStorageException extends HeraclesStorageException {

	public StreamNotAvailableStorageException(String message) {
		super(message, ErrorCode.STREAM_UNAVAILABLE.getCode());
	}

	public StreamNotAvailableStorageException(String message, Throwable cause) {
		super(message, cause, ErrorCode.STREAM_UNAVAILABLE.getCode());
	}
}
