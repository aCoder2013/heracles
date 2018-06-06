package com.song.heracles.store.core.support;

import com.song.heracles.common.constants.ErrorCode;

import org.apache.distributedlog.DLSN;

import lombok.Getter;
import lombok.Setter;

/**
 * @author song
 */
@Getter
@Setter
public class WriteResponse extends StreamResponse {

	private DLSN dlsn;

	public WriteResponse() {
	}

	public WriteResponse(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
