package com.song.heracles.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author song
 */
public class MathUtilsTest {

	@Test
	public void safeMod() {
		int i = MathUtils.safeMod(Integer.MIN_VALUE, 1);
		assertThat(i).isGreaterThanOrEqualTo(0);
	}
}