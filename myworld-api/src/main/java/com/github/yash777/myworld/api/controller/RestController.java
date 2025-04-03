package com.github.yash777.myworld.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>@GetMapping - Acts as a shortcut for @RequestMapping(method = RequestMethod.GET).
 * <li>HTTP MediaType: Use Map<String, String> or a DTO object (@RestController will automatically convert it to JSON).
 * <li>HTTP MediaType: If return type is String then default response header is 
 * <pre class="code">Content-Type = text/plain;charset=UTF-8</pre>, 
 * or we need to explicitly specify MediaType as MediaType.APPLICATION_JSON_VALUE.
 * </ul>
 * 
 * @author yashwanth
 *
 */
@org.springframework.web.bind.annotation.RestController //spring-boot-starter-web
@org.springframework.web.bind.annotation.RequestMapping(value = "/sample")
@Slf4j
public class RestController {
	{
		log.info("RestController Bean created.");
		System.out.println("RestController Bean created.");
	}

	@RequestMapping(value = "/text", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String getSampleText() {
		log.info("Get Sample response as text.");
		System.out.println("Get Sample response as text.");
		return "sample text response";
	}
	
	/*
> Best Approach?
✅ Use Map<String, String> or a DTO object (@RestController will automatically convert it to JSON).
✅ If returning a raw JSON string, use ResponseEntity<String> with MediaType.APPLICATION_JSON.
❌ Avoid returning a raw String unless necessary (it defaults to text/plain).
	 */
	// Using a Wrapper Object (Recommended)
	@GetMapping("/json/map-string")
	public Map<String, String> getSampleJsonAsObject() {
		return Map.of("message", "Hello, World!");
	}
	
	// Using ResponseEntity<String> with MediaType.APPLICATION_JSON
	@GetMapping(value = "/json/responseEntity-string", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSampleJsonAsResponseEntityString() {
		String json = "{\"message\": \"Hello, World!\"}";
		//HttpHeaders responseHeaders = new HttpHeaders();
		//responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		//return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
		return new ResponseEntity<>(json, HttpStatus.OK);
	}
	// Returning JSON-String with MediaType.APPLICATION_JSON
	@GetMapping(value = "/json/string", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getSampleJsonAsString() {
		String json = "{\"message\": \"Hello, World!\"}";
		return json;
	}
	@GetMapping(value = "/json-string") // HTTP MediaType: Default response header Content-Type = text/plain;charset=UTF-8
	public String getSampleString() {
		String json = "{\"message\": \"Hello, World!\"}";
		return json;
	}
	
	
	
}
