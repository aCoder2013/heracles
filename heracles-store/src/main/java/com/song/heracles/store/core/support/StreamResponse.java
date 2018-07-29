package com.song.heracles.store.core.support;

import com.song.heracles.common.constants.ErrorCode;

/**
 * @author song
 */
public class StreamResponse {

    private ErrorCode code;

    private String message;

    public StreamResponse() {
    }

    public StreamResponse(ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
