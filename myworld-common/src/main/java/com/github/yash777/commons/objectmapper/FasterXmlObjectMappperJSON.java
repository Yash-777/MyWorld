package com.github.yash777.commons.objectmapper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FasterXmlObjectMappperJSON {
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
		//objectMapper.writer(new DefaultPrettyPrinter());
		return objectMapper;
	}
	
	static String payLoadJsonFile = "D:/emp.json";
	public static void main(String[] args) throws StreamReadException, DatabindException, IOException {
		FasterXmlObjectMappperJSON obj = new FasterXmlObjectMappperJSON();
		obj.test();
		
	}
	public void test() throws StreamReadException, DatabindException, IOException {
		Employee e = Employee.builder()
				.id(777).age(30)
				.name("Yashwanth").first("Yash").last("M")
				.dob( new Date() )
				//.address("HYD")
				.dd( Dates.builder().d1( new Date() )
						.dd( Dates.builder().d1( new Date() ).build() )
						.build() )
				.build();
		System.out.println("emp:"+e);
		
		Object jsonObj = e;
		
		ObjectMapper objectMapper = getObjectMapper();
		// ✅ WRITE_DATES_AS_TIMESTAMPS should be false for readable date output
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		String writeValueAsString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
		System.out.println("writeValueAsString:"+writeValueAsString);
		
		Object readValue = objectMapper.readValue(writeValueAsString, Employee.class);
		System.out.println("readValue:"+readValue);
		
		this.writeObj(e);
		
		Employee readJson = this.readJson();
		System.out.println("readJson:"+readJson);
	}
	public File getFile(String payLoadJsonFile) {
		return new File(payLoadJsonFile);
		//new java.io.File(getClass().getClassLoader().getResource( payLoadJsonFile ).getFile());
	}
	public Boolean writeObj(Employee result) throws StreamWriteException, DatabindException, IOException {
		File file = getFile(payLoadJsonFile);
		ObjectMapper objectMapper = getObjectMapper();
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, result); // write : "dob" : 1752495563341
		try {
			String nodeName = "name", nodeDOB = "dob";// Employee.name, Employee.dob
			JsonNode readTree = objectMapper.readTree(file);
			if (readTree.get(nodeName) != null) { 
				System.out.println("Specific Node:"+readTree.get(nodeName).toString());
			}
			if (readTree.get(nodeDOB) != null) { 
				System.out.println("Specific Node:"+readTree.get(nodeDOB).toString()); // json : dob=Mon Jul 14 17:49:23 IST 2025
			}
			System.out.println("Full Tree:\n"+readTree);
			System.out.println("Full Tree PrettyString:\n"+readTree.toPrettyString());
			return Boolean.TRUE;
		} catch (JsonProcessingException e) {
			return Boolean.FALSE;
		}
	}
	public Employee readJson() throws StreamReadException, DatabindException, IOException {
		File file = getFile(payLoadJsonFile);
		ObjectMapper objectMapper = getObjectMapper();
		
		// ✅ WRITE_DATES_AS_TIMESTAMPS should be false for readable date output
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		return objectMapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<Employee>(){});
	}
	
}
@Data @Builder @AllArgsConstructor @NoArgsConstructor @ToString
//@lombok.NoArgsConstructor(lombok.AccessLevel.PRIVATE)
class Employee {
	int id, age;
	String name, first, last, address;
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy, h:mm:ss a")
	Date dob;
	
	Dates dd;
}
@Data @Builder @AllArgsConstructor @NoArgsConstructor @ToString
class Dates {
	Date d1;
	
	Dates dd;
}

