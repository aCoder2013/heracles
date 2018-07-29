package com.song.heracles.common.exception;

import com.song.heracles.common.constants.ErrorCode;

/**
 * @author song
 */
public class HeraclesException extends Exception {

    private int code;

    public HeraclesException(Throwable throwable) {
        this(throwable, ErrorCode.UNKNOWN_ERROR.getCode());
    }

    public HeraclesException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HeraclesException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public HeraclesException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
