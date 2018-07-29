package com.song.heracles.broker.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.distributedlog.DLSN;

/**
 * @author song
 */
@Data
@AllArgsConstructor
public class Offset {

    private DLSN dlsn;
}
