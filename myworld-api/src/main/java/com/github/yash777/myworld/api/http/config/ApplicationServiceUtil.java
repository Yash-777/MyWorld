package com.github.yash777.myworld.api.http.config;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Service
public class ApplicationServiceUtil {
	String API_FullDateFormat = "dd.MM.yyyy'T'HH:mm:ss.SSS'Z'";
	String API_ShortDateFormat = "dd.MM.yyyy";
	
	public void configureObjectMapper(ObjectMapper mapper, String dateFormat) {
		SimpleModule simpleModule = new com.fasterxml.jackson.databind.module.SimpleModule();
		simpleModule.addDeserializer(java.util.Date.class, new CustomDateStdDeserializer());
		mapper.registerModules(simpleModule);
		
		if (StringUtils.isBlank(dateFormat)) {
			mapper.setDateFormat(new SimpleDateFormat(API_FullDateFormat));
		} else {
			if (StringUtils.equals(dateFormat, API_ShortDateFormat)) {
				mapper.setDateFormat(new SimpleDateFormat(API_ShortDateFormat));
			} else {
				mapper.setDateFormat(new SimpleDateFormat(API_FullDateFormat));
			}
		}
	}
	
	public String convertObjectToJson(Object obj) {
		String json = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
		}
		return json;
	}
}
