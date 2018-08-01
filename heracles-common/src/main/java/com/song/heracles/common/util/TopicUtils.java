package com.song.heracles.common.util;

import com.song.heracles.common.exception.InvalidTopicException;
import org.apache.commons.lang3.StringUtils;

public class TopicUtils {

    private static final int MAX_NAME_LENGTH = 249;

    public static void validate(String topic) {
        if (StringUtils.isBlank(topic)) {
            throw new InvalidTopicException("Topic name is illegal, it can't be empty");
        }

        if (topic.equals(".") || topic.equals("..")) {
            throw new InvalidTopicException("Topic name cannot be \".\" or \"..\"");
        }

        if (topic.length() > MAX_NAME_LENGTH) {
            throw new InvalidTopicException(
                "Topic name is illegal, it can't be longer than " + MAX_NAME_LENGTH +
                    " characters, topic name: " + topic);
        }

        if (!containsValidPattern(topic)) {
            throw new InvalidTopicException(
                "Topic name \"" + topic + "\" is illegal, it contains a character other than " +
                    "ASCII alphanumerics, '.', '_' and '-'");
        }
    }

    public static boolean hasCollisionChars(String topic) {
        return topic.contains("_") || topic.contains(".");
    }

    /**
     * Valid characters for Kafka topics are the ASCII alphanumerics, '.', '_', and '-'
     */
    private static boolean containsValidPattern(String topic) {
        for (int i = 0; i < topic.length(); ++i) {
            char c = topic.charAt(i);

            // We don't use Character.isLetterOrDigit(c) because it's slower
            boolean validChar =
                (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')
                    || c == '.' ||
                    c == '_' || c == '-';
            if (!validChar) {
                return false;
            }
        }
        return true;
    }

}
