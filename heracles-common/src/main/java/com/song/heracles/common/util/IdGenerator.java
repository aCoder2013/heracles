package com.song.heracles.common.util;

/**
 * @author song
 */
public interface IdGenerator {

	long INVALID_TXID = -9999;

	long nextId();

}
