package com.song.heracles.client.configuration;

import com.song.heracles.common.util.Required;

import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author song
 */
@Setter
@Getter
@Builder
public class ProducerConfiguration {

	@Required
	private String topic;

	private String producerName = "";

	private long startTimeout = 6000;

	private TimeUnit startTimeoutUnit = TimeUnit.MILLISECONDS;
}
