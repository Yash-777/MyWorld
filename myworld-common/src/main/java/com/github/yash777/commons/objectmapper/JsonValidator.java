package com.github.yash777.commons.objectmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonValidator {

    // Create and configure ObjectMapper
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    // Validate and return compact JSON or error
    public String validateAndCompactJson(String jsonInput) {
        ObjectMapper objectMapper = getObjectMapper();

        try {
            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(jsonInput);

            // Return compact JSON
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // Return user-friendly error message
            return "❌ Invalid JSON at line " + e.getLocation().getLineNr()
                   + ", column " + e.getLocation().getColumnNr()
                   + ": " + e.getOriginalMessage();
        }
    }

    // Example usage
    public static void main(String[] args) {
        JsonValidator validator = new JsonValidator();

        // ✅ Valid JSON Example
        String validJson = "{\n" +
                "  \"id\": 101,\n" +
                "  \"name\": \"Alice\",\n" +
                "  \"dob\": \"2025-10-04T10:15:30\"\n" +
                "}";
        String result1 = validator.validateAndCompactJson(validJson);
        System.out.println("Valid JSON Result:\n" + result1);

        // ❌ Invalid JSON Example (missing closing brace)
        String invalidJson = "{\n" +
                "  \"id\": 102,\n" +
                "  \"name\": \"Bob\",\n" +
                "  \"dob\": \"2025-10-04T10:15:30\"\n";
        String result2 = validator.validateAndCompactJson(invalidJson);
        System.out.println("Invalid JSON Result:\n" + result2);
    }
}
