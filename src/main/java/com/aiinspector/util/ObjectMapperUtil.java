package com.aiinspector.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {
	
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }
	
	private ObjectMapperUtil() {
		
	}

	public static ObjectMapper getObjectmapper() {
		return objectMapper;
	}
	
	

}
