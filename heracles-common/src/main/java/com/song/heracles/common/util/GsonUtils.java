package com.song.heracles.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class GsonUtils {

	private static final Gson gson = new GsonBuilder().create();

	public static String toJson(Object obj) {
		try {
			return gson.toJson(obj);
		} catch (Exception e) {
			log.error("To json failed", e);
			return null;
		}
	}

}
