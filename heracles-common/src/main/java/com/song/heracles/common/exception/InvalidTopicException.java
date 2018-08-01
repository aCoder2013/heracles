package com.song.heracles.common.exception;

import com.song.heracles.common.constants.ErrorCode;

public class InvalidTopicException extends AdminException {

    public InvalidTopicException(String message) {
        super(message, ErrorCode.INVALID_TOPIC.getCode());
    }

    public InvalidTopicException(Throwable cause) {
        super(cause, ErrorCode.INVALID_TOPIC.getCode());
    }

    public InvalidTopicException(String message, Throwable cause) {
        super(message, cause, ErrorCode.INVALID_TOPIC.getCode());
    }
}
