package com.song.heracles.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class NetUtils {

    public static String getLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Fail to resolve localhost : " + e.getMessage(), e);
            throw new IllegalStateException("Failed to resolve localhost .", e);
        }
    }
}
