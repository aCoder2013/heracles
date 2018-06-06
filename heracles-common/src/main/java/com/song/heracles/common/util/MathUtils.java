package com.song.heracles.common.util;

import java.util.concurrent.TimeUnit;

/**
 * @author song
 */
public class MathUtils {

	public static long now() {
		return System.nanoTime() / 1000000L;
	}

	public static long nowInNano() {
		return System.nanoTime();
	}

	public static long elapsedMSec(long startNanoTime) {
		return (System.nanoTime() - startNanoTime) / 1000000L;
	}

	public static long elapsedMicroSec(long startNanoTime) {
		return TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startNanoTime);
	}

	public static long elapsedNanos(long startNanoTime) {
		return System.nanoTime() - startNanoTime;
	}

	public static int safeMod(long dividend, int divisor) {
		int mod = (int) (dividend % divisor);

		if (mod < 0) {
			mod += divisor;
		}

		return mod;
	}

}
