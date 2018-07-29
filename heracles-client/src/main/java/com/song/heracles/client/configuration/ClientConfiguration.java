package com.song.heracles.client.configuration;

import com.song.heracles.common.util.Required;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author song
 */
@Setter
@Getter
@Builder
public class ClientConfiguration {

    @Required
    private List<String> servers;

    private long operationTimeoutMills = 30_000;
}
