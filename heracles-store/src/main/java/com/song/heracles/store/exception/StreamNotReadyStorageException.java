package com.song.heracles.store.exception;

import com.song.heracles.common.constants.ErrorCode;

/**
 * @author song
 */
public class StreamNotReadyStorageException extends HeraclesStorageException {


	public StreamNotReadyStorageException(String message) {
		super(message, ErrorCode.STREAM_NOT_READY.getCode());
	}

	public StreamNotReadyStorageException(String message, Throwable cause) {
		super(message, ErrorCode.STREAM_NOT_READY.getCode());
	}
}
