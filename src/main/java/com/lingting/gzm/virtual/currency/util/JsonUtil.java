package com.lingting.gzm.virtual.currency.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author lingting 2020-09-02 17:12
 */
@Slf4j
public class JsonUtil {

	private static final ObjectMapper OM;

	static {
		OM = new ObjectMapper();
		OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public static <T> T readValue(String jsonStr, Class<T> t) {
		try {
			return OM.readValue(jsonStr, t);
		}
		catch (IOException e) {
			log.error("json转对象失败", e);
			return null;
		}
	}

}