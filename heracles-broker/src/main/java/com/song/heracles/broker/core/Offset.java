package com.song.heracles.broker.core;

import org.apache.distributedlog.DLSN;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author song
 */
@Data
@AllArgsConstructor
public class Offset {

	private DLSN dlsn;
}
