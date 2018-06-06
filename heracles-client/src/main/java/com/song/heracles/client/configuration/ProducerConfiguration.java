package com.song.heracles.client.configuration;

import com.song.heracles.common.util.Required;

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

}
