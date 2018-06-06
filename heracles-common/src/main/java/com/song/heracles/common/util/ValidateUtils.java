package com.song.heracles.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author song
 */
public class ValidateUtils {

	public static final String TOPIC_PATTERN_STR = "^[%|a-zA-Z0-9_-]+$";

	public static final Pattern PATTERN = Pattern.compile(TOPIC_PATTERN_STR);

	public static final int CHARACTER_MAX_LENGTH = 255;

	public static Result checkTopicName(String topic) {
		Result result = new Result();
		if (StringUtils.isBlank(topic)) {
			result.setMessage("Topic can't be null or empty.");
			return result;
		}
		if (!PATTERN.matcher(topic).matches()) {
			result.setMessage(String.format("Topic contains invalid characters, on allow: %s.", TOPIC_PATTERN_STR));
			return result;
		}
		if (topic.length() > CHARACTER_MAX_LENGTH) {
			result.setMessage(String.format("Topic name is too long , allowed max length is %d.", CHARACTER_MAX_LENGTH));
			return result;
		}
		result.setSuccess(true);
		return result;
	}

}
