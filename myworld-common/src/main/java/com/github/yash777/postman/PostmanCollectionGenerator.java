package com.github.yash777.postman;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.github.yash777.commons.lang.SystemEnvUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <p>A utility class to generate a Postman collection JSON file from a CSV input.
 * <br/>The CSV file consists of inbound request logs captured from application filter aspects.
 * </p>
 * 
 * If the CSV file doesn't exist, it creates one with sample data.
 * 
 * @author 🔐 Yash
 */
public class PostmanCollectionGenerator {
	
	public static final String PROTOCOL_HTTP = "http", PROTOCOL_HTTPS = "https";
	
	static String protocal = PROTOCOL_HTTP;
	static String[] headers = {"OBJECT ID", "REQUEST"}; // Expected CSV headers
	
	public static void main(String[] args) {
		String path = SystemEnvUtil.getTempLocation()+"\\Python_DataMigration\\";
		String csvFilePath = path + "Inbound_Logs.csv";
		String jsonFilePath = path + "my_collection.json";
		
		try {
			ensureSampleCsvExists(csvFilePath); // Create file if not present
			
			List<Map<String, Object>> requests = readCSV(csvFilePath);
			Map<String, Object> postmanCollection = createPostmanCollection(requests);
			writeToJsonFile(postmanCollection, jsonFilePath);
			
			System.out.println("Postman collection generated: " + jsonFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads CSV records and returns a list of request data.
	 *
	 * @param filePath path to the CSV file
	 * @return list of request maps containing object_id and request_payload
	 * @throws IOException if reading the file fails
	 */
	private static List<Map<String, Object>> readCSV(String filePath) throws IOException {
		CSVFormat csvFormat = CSVFormat.EXCEL.builder()
				.setHeader(headers)       // Use expected headers
				.setSkipHeaderRecord(true) // Skip first row
				.build();
		
		List<Map<String, Object>> requests = new ArrayList<>();
		
		try (FileReader reader = new FileReader(filePath);
				CSVParser csvParser = new CSVParser(reader, csvFormat)) {
			
			for (CSVRecord record : csvParser) {
				Map<String, Object> requestData = new LinkedHashMap<>();
				requestData.put("object_id", record.get("OBJECT ID"));
				requestData.put("request_payload", record.get("REQUEST"));
				requests.add(requestData);
			}
		}
		
		return requests;
	}
	
	/**
	 * Creates a Postman collection map structure from parsed CSV data.
	 *
	 * @param requests List of request maps with object_id and payload
	 * @return a complete Postman collection map
	 */
	private static Map<String, Object> createPostmanCollection(List<Map<String, Object>> requests) {
		Map<String, Object> postmanCollection = new LinkedHashMap<>();
		Map<String, String> info = new LinkedHashMap<>();
		info.put("name", "Generated Collection");
		info.put("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
		postmanCollection.put("info", info);
		
		List<Map<String, Object>> items = new ArrayList<>();
		
		for (Map<String, Object> requestData : requests) {
			String objectId = (String) requestData.get("object_id");
			String requestPayload = (String) requestData.get("request_payload");
			
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("name", "Request for OBJECT ID " + objectId);
			
			Map<String, Object> request = new LinkedHashMap<>();
			
			List<Map<String, String>> headers = new ArrayList<>();
			addHeader(headers, "Authorization", "{{token}}", "text");
			addHeader(headers, "Cookie", "{{session ID}}", "text");
			addHeader(headers, "Content-Type", "application/json", "text");
			
			Map<String, Object> url = new LinkedHashMap<>();
			url.put("raw", "{{host}}/client/participant/" + objectId);
			url.put("protocol", Arrays.asList(protocal));
			url.put("host", Arrays.asList("{{host}}"));
			url.put("path", Arrays.asList("client", "participant", objectId));
			
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("mode", "raw");
			body.put("raw", requestPayload);
			
			request.put("method", "PUT");
			request.put("header", headers);
			request.put("url", url);
			request.put("body", body);
			
			item.put("request", request);
			item.put("response", new ArrayList<>()); // empty response
			
			items.add(item);
		}
		
		postmanCollection.put("item", items);
		return postmanCollection;
	}
	
	/**
	 * Adds a header key-value to the header list for Postman request.
	 */
	private static void addHeader(List<Map<String, String>> headers, String key, String value, String type) {
		Map<String, String> header = new LinkedHashMap<>();
		header.put("key", key);
		header.put("value", value);
		header.put("type", type);
		headers.add(header);
	}
	
	/**
	 * Writes the final Postman collection map to a JSON file.
	 *
	 * @param postmanCollection collection object
	 * @param filePath output file path
	 * @throws IOException if writing fails
	 */
	private static void writeToJsonFile(Map<String, Object> postmanCollection, String filePath) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter(filePath)) {
			gson.toJson(postmanCollection, writer);
		}
	}
	
	/**
	 * Checks if the CSV file exists, if not creates one with sample data.
	 *
	 * @param filePath path to the CSV file
	 * @throws IOException if file write fails
	 */
	private static void ensureSampleCsvExists(String filePath) throws IOException {
		File file = new File(filePath);
		File parentDir = file.getParentFile();
		
		if (!parentDir.exists()) {
			if (parentDir.mkdirs()) {
				System.out.println("Created directory: " + parentDir.getAbsolutePath());
			} else {
				throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
			}
		}
		
		if (!file.exists()) {
			String sampleCsv = """
					"OBJECT ID","REQUEST"
					"123","{\\"json\\":{\\"country\\":\\"PL\\"}}"
					"234","{\\"json\\":{\\"country\\":\\"DE\\"}}"
					""";
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(sampleCsv);
			}
			
			System.out.println("Sample CSV file created at: " + filePath);
		}
	}
}
