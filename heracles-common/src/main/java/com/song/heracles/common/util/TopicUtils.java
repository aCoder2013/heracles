package com.song.heracles.common.util;

public class TopicUtils {

    public static boolean hasCollisionChars(String topic) {
        return topic.contains("_") || topic.contains(".");
    }

}
