package com.song.heracles.store.exception;

import com.song.heracles.common.exception.HeraclesException;

/**
 * @author song
 */
public class HeraclesStorageException extends HeraclesException {

    private int code;

    public HeraclesStorageException(String message, int code) {
        super(message, code);
        this.code = code;
    }

    public HeraclesStorageException(String message, Throwable cause, int code) {
        super(message, cause, code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
