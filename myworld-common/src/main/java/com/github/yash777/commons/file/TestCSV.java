package com.github.yash777.commons.file;

import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;

/*
 * If your map is not working when it contains UTF-8 characters (e.g., non-ASCII characters like Chinese, Arabic, accented letters, emojis, etc.), it’s likely due to encoding or parsing issues depending on what you mean by "map" and what environment you're working in.

Use a CSV Parser Library - To properly read quoted CSV data, you should not use String.split(). Use a proper CSV parsing library like:
email,state,country,phone,name
"juan@example,com",Madrid,España,1234567890,Juan Álvarez
rama@example.com,తెలంగాణ,భారత్,9876543210,రామకృష్ణ

For Below Data Mismatch
"juan@example.com" - No Issue
"juan@example,com" - Issue with String.split()
*/
public class TestCSV {
	public static void main(String[] args) {
		String filePath = TestCSV.class.getClassLoader()
			    .getResource("utf8Data.csv")
			    .getPath();

		System.out.println("File path: " + filePath);
		//String filePath = "C:\\Users\\ymerugu\\Downloads/utf8Data.csv";
		
		List<Map<String, String>> csvData = readDataFromCSV(filePath, true);
		display(csvData);
		
		List<Map<String, String>> csvDataWithQuotes = readCsvWithQuotes(filePath, true);
		display(csvDataWithQuotes);
	}
	public static void display(List<Map<String, String>> csvData) {
		System.out.println("✅ ---- CSV Data Loaded ----");
		for (Map<String, String> row : csvData) {
			System.out.println("Row: "+row);
		}
		
		// Print values using exact header names
		if (!csvData.isEmpty()) {
			Map<String, String> row = csvData.get(0);
			Set<String> keys = row.keySet();
			System.out.println("🗝️ Keys: " + keys);
			
			// Replace these keys with your actual headers (case-sensitive!)
			if (row.containsKey("email")) System.out.println("📧 Email: " + row.get("email"));
			if (row.containsKey("state")) System.out.println("🏙️ State: " + row.get("state"));
			if (row.containsKey("country")) System.out.println("🌍 Country: " + row.get("country"));
			if (row.containsKey("phone")) System.out.println("📞 Phone: " + row.get("phone"));
		}
	}
	
	public static List<Map<String, String>> readDataFromCSV(String filePath, boolean keysLowerCase) {
		List<Map<String, String>> csvData = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(new FileInputStream(new File(filePath)), java.nio.charset.StandardCharsets.UTF_8))) {
			String headerLine = br.readLine();
			if (headerLine == null) {
				throw new RuntimeException("CSV file is empty!");
			}
			
			// Handle UTF-8 BOM (Byte Order Mark)
			//headerLine = headerLine.replace("\uFEFF", "");
			
			String[] headers = Arrays.stream(headerLine.split(","))
					.map(String::trim)
					.toArray(String[]::new);
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = 
						line.split(",");
				//line.split(",", -1); // Use -1 to preserve trailing empty fields
				Map<String, String> row = new LinkedHashMap<>(); // LinkedHashMap preserves order
				
				for (int i = 0; i < headers.length; i++) {
					String key = headers[i];
					if (keysLowerCase) {
						key = key.toLowerCase();
					}
					String value = (i < values.length) ? values[i].trim() : "";
					row.put(key, value);
				}
				csvData.add(row);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return csvData;
	}
	
	public static List<Map<String, String>> readCsvWithQuotes(String filePath, boolean keysLowerCase) {
		List<Map<String, String>> result = new ArrayList<>();
		
		try (CSVReader reader = new CSVReader(
				new FileReader(filePath, java.nio.charset.StandardCharsets.UTF_8))) {
			
			String[] headers = reader.readNext();
			if (headers == null) {
				throw new RuntimeException("CSV is empty");
			}
			
			String[] line;
			while ((line = reader.readNext()) != null) {
				Map<String, String> row = new LinkedHashMap<>();
				for (int i = 0; i < headers.length; i++) {
					String key = headers[i].trim().toLowerCase();
					String value = (i < line.length) ? line[i].trim() : "";
					row.put(key, value);
				}
				result.add(row);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
