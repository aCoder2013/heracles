package com.song.heracles.broker.core.zk;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicConfig implements Serializable {

    private String topicName;

    private int partitions;
}
