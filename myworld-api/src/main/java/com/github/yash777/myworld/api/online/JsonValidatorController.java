package com.github.yash777.myworld.api.online;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 * ✅ JSON Validator API
 * 
 * This controller exposes functionality to:
 * 
 * <ul>
 * <li>✔ Validate a raw JSON string dynamically using Jackson</li>
 * <li>📦 Return a compact single-line version of the JSON if it's valid</li>
 * <li>❌ Return a clear, user-friendly error message if invalid (includes line/column info)</li>
 * </ul>
 * 
 * 🔍 Test your JSON online:
 * 
 * <ul>
 * <li><a href="https://jsonlint.com/">JSONLint</a></li>
 * <li><a href="https://jsonformatter.org/">JSON Formatter</a></li>
 * <li><a href="https://codebeautify.org/jsonvalidator">CodeBeautify Validator</a></li>
 * </ul>
 * 
 * 📌 Usage Modes:
 * 
 * <ul>
 * <li>✅ Spring Boot REST endpoint (via <code>@RestController</code>)</li>
 * <li>✅ Standalone Java CLI via <code>main()</code> method</li>
 * </ul>
 * 
 * @author 🔐 Yashwanth
 */
@Tag(name = "Online Module", description = "Online Module APIs for JSON, XML, Text")
@RestController
@RequestMapping("/online/jsonapi")
public class JsonValidatorController {
	
	private final ObjectMapper objectMapper;
	
	public JsonValidatorController() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
	}
	
	/**
	 * Validate JSON and return compact single-line JSON or error message.
	 *
	 * @param jsonInput The input JSON string
	 * @return Compact JSON or error message
	 */
	@PostMapping("/validate") // http.csrf().disable(); → For REST APIs, CSRF can be disabled
	//@GetMapping("/validate")
	public String validateJson(
			@Parameter(description = "Raw JSON string", example = """
			{
				"name": "Yash",
				"age": 30
			}
			""")
			@RequestParam(required = true)
			String jsonInput
			) {
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonInput);
			return objectMapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			return "❌ Invalid JSON at line " + e.getLocation().getLineNr()
					+ ", column " + e.getLocation().getColumnNr()
					+ ": " + e.getOriginalMessage();
		}
	}
	
	/**
	 * Standalone Java main method for testing JSON validation.
	 */
	public static void main(String[] args) {
		JsonValidatorController validator = new JsonValidatorController();
		
		// ✅ Valid JSON using text block
		String validJson = """
		{
			"id": 101,
			"name": "Alice",
			"dob": "2025-10-04T10:15:30"
		}
		""";
		System.out.println("✅ Valid JSON:\n" + validator.validateJson(validJson));
		
		// ❌ Invalid JSON using text block (missing closing brace)
		String invalidJson = """
		{
			"id": 102,
			"name": "Bob",
			"dob": "2025-10-04T10:15:30"
		""";
		System.out.println("❌ Invalid JSON:\n" + validator.validateJson(invalidJson));
	}
}