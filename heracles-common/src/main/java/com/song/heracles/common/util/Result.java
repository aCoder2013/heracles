package com.song.heracles.common.util;

import lombok.Getter;
import lombok.Setter;

/**
 * @author song
 */
@Setter
@Getter
public class Result<T> {

    private boolean success;

    private T data;

    private String message;

    private Throwable throwable;

}
