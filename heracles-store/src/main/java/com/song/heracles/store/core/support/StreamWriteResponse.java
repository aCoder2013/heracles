package com.song.heracles.store.core.support;

import org.apache.distributedlog.DLSN;

/**
 * @author song
 */
public class StreamWriteResponse extends StreamResponse {

    private DLSN dlsn;

    public DLSN getDlsn() {
        return dlsn;
    }

    public void setDlsn(DLSN dlsn) {
        this.dlsn = dlsn;
    }
}
