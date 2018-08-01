package com.song.heracles.common.exception;

public class AdminException extends HeraclesException{


    public AdminException(Throwable throwable) {
        super(throwable);
    }

    public AdminException(String message, int code) {
        super(message, code);
    }

    public AdminException(Throwable cause, int code) {
        super(cause, code);
    }

    public AdminException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }
}
