package com.github.yash777.commons.objectmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//https://stackoverflow.com/questions/64036/how-do-you-make-a-deep-copy-of-an-object
public class FasterXmlObjectMapperUtil {
    //https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html
    private static final ObjectMapper objectMapper; // = new ObjectMapper();
    static {
    	objectMapper = getObjectMapper();
    }
    public static ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
		objectMapper.setDefaultPrettyPrinter(new com.fasterxml.jackson.core.util.DefaultPrettyPrinter());
		
		// Configure to skip comments in payloads
		objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, false);

		return objectMapper;
	}
    
    // Write Functions

    // Convert Object to JSON String
    public static String objectToJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    // Convert Object to JSONNode
    public static JsonNode objectToJsonNode(Object obj) throws Exception {
        return objectMapper.valueToTree(obj);
    }

    // Deep clone an object using serialization/deserialization
    public static <T> T deepClone(Object obj, Class<T> type) throws Exception {
        String jsonString = objectMapper.writeValueAsString(obj);
        return objectMapper.readValue(jsonString, type);
    }

    // Read Functions

    // Convert JSON String to Object of specified type
    public static <T> T jsonStringToObject(String jsonString, Class<T> type) throws Exception {
        return objectMapper.readValue(jsonString, type);
    }

    public static void main(String[] args) {
        try {
            // Example Usage
            // Write Functions
            MyObject myObject = new MyObject("example", 42);
            
            // Object to JSON String
            String jsonString = objectToJsonString(myObject);
            System.out.println("Object to JSON String: " + jsonString);

            // Object to JSONNode
            JsonNode jsonNode = objectToJsonNode(myObject);
            System.out.println("Object to JSONNode: " + jsonNode);

            // Object to Object DeepClone
            MyObject clonedObject = deepClone(myObject, MyObject.class);
            System.out.println("Object to DeepClone: " + clonedObject);
            
            System.out.println("Object to DeepClone: OldHash:" + System.identityHashCode(myObject) +", NewHash:"+System.identityHashCode(clonedObject));
            myObject.setName("old");
            clonedObject.setName("new");
            System.out.println(myObject.toString() +" :: "+ clonedObject.toString());
            
            
            // Read Functions
            String json = "{\"name\":\"example\",\"value\":42}";

            // JSON String to Object
            MyObject deserializedObject = jsonStringToObject(json, MyObject.class);
            System.out.println("JSON String to Object: " + deserializedObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Builder @Data @AllArgsConstructor @NoArgsConstructor
class MyObject {
    private String name;
    private int value;

    @Override
    public String toString() {
        return "MyObject{ name='" + name + "' value=" + value + '}';
    }
}
