package com.song.heracles.store.core.support;

/**
 * @author song
 */
public enum StatusCode {
    OK(0),
    OWNER_ALREADY_EXIST(1),;

    private int code;

    StatusCode(int code) {
        this.code = code;
    }
}
