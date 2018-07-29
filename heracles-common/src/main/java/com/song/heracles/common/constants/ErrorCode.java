package com.song.heracles.common.constants;

/**
 * @author song
 */
public enum ErrorCode {
    OK(0, "ok"),
    STREAM_UNAVAILABLE(1001, "Stream is not available"),
    STREAM_NOT_READY(1002, "Stream is not ready"),
    OWNER_ALREADY_EXIST(1003, "Owner already exist"),
    ILLEGAL_STATE(1004, "Illegal state"),
    RESOURCE_NOT_READY(1004, "Resource is not ready yet"),
    UNKNOWN_ERROR(9999, "unknown error"),;

    private int code;

    private String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return ErrorCode.UNKNOWN_ERROR;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
