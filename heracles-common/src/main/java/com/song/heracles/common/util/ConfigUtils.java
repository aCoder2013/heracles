package com.song.heracles.common.util;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author song
 */
public class ConfigUtils {

	public static void validate(Object configuration) throws IllegalArgumentException {
		Preconditions.checkArgument(configuration != null);
		Field[] fields = configuration.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Required.class)) {
				field.setAccessible(true);
				Object val = null;
				try {
					val = field.get(configuration);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				if (isEmpty(val)) {
					throw new IllegalArgumentException(String.format("Required field %s is null or empty.", field.getName()));
				}
			}
		}
	}

	private static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String) {
			return StringUtils.isBlank((String) obj);
		} else if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		} else {
			return false;
		}
	}


}
