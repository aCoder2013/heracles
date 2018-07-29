package com.song.heracles.client.configuration;


import com.song.heracles.common.util.Required;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ConsumerConfiguration {

    @Required
    private String topic;

    private String consumerName;

    private long startTimeout = 6000;

    private TimeUnit startTimeoutUnit = TimeUnit.MILLISECONDS;
}
