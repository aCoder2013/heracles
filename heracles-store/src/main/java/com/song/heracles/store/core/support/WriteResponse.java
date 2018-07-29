package com.song.heracles.store.core.support;

import com.song.heracles.common.constants.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.distributedlog.DLSN;

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
