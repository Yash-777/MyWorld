package com.github.yash777.basic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class for JSON-related operations.
 * 
 * @author 🔐 Yashwanth
 */
public class JsonUtil {
	
	private static final ObjectMapper mapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);
	
	/**
	 * Converts any object to a pretty-printed JSON string.
	 *
	 * @param obj the object to convert
	 * @return pretty JSON string
	 */
	public static String toPrettyJson(Object obj) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return "Failed to convert object to JSON: " + e.getMessage();
		}
	}
}
