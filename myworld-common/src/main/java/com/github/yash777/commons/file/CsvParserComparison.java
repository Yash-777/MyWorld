package com.github.yash777.commons.file;

import java.io.*;
import java.util.*;

import com.opencsv.CSVReader;

/**
 * ✅ Demonstrates reading UTF-8 encoded CSV files using two approaches:
 * 
 * <ul>
 *   <li><b>BufferedReader + String.split()</b> - Basic approach. Does NOT handle quoted fields correctly.</li>
 *   <li><b>OpenCSV CSVReader</b> - Robust parser. Correctly handles quoted values, embedded commas, and UTF-8 characters.</li>
 * </ul>
 * 
 * <h2>📄 Example CSV Content (utf8Data.csv)</h2>
 * <pre>{@code
 * email,state,country,phone,name
 * "juan@example,com",Madrid,España,1234567890,Juan Álvarez
 * rama@example.com,తెలంగాణ,భారత్,9876543210,రామకృష్ణ
 * }</pre>
 * 
 * <h3>⚠️ Common Mismatch Example</h3>
 * <ul>
 *   <li>"juan@example.com" → ✅ Parsed correctly by both BufferedReader and CSVReader</li>
 *   <li>"juan@example,com" → ❌ Incorrectly split by BufferedReader due to comma inside quotes</li>
 * </ul>
 *
 * <h3>💡 Recommendation:</h3>
 * Always use <b>OpenCSV's CSVReader</b> for reading real-world CSVs containing:
 * <ul>
 *   <li>Quoted fields</li>
 *   <li>Special characters (emojis, non-English scripts)</li>
 *   <li>Embedded commas or newlines in fields</li>
 * </ul>
 */
public class CsvParserComparison {
	//Emphasizes the comparison between BufferedReader and CSVReader
	public static void main(String[] args) {
		String filePath = CsvParserComparison.class.getClassLoader()
				.getResource("utf8Data.csv")
				.getPath();
		
		System.out.println("📂 File path: " + filePath);
		
		// ❌ Using BufferedReader - not quote-aware
		List<Map<String, String>> csvDataRaw = readUsingBufferedReader(filePath, true);
		System.out.println("🔎 Parsed using BufferedReader (⚠️ not quote-safe):");
		display(csvDataRaw);
		
		// ✅ Using OpenCSV - quote-aware
		List<Map<String, String>> csvDataParsed = readUsingCsvReader(filePath, true);
		System.out.println("✅ Parsed using CSVReader (quote-safe, UTF-8 safe):");
		display(csvDataParsed);
	}
	
	public static List<Map<String, String>> readUsingBufferedReader(String filePath, boolean keysLowerCase) {
		List<Map<String, String>> csvData = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
			
			String headerLine = br.readLine();
			if (headerLine == null) throw new RuntimeException("CSV file is empty!");
			
			// Remove UTF-8 BOM if present
			headerLine = headerLine.replace("\uFEFF", "");
			
			String[] headers = Arrays.stream(headerLine.split(","))
					.map(String::trim)
					.toArray(String[]::new);
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(","); // ❌ Not safe for quoted fields
				Map<String, String> row = new LinkedHashMap<>();
				for (int i = 0; i < headers.length; i++) {
					String key = keysLowerCase ? headers[i].toLowerCase() : headers[i];
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
	
	public static List<Map<String, String>> readUsingCsvReader(String filePath, boolean keysLowerCase) {
		List<Map<String, String>> result = new ArrayList<>();
		
		try (CSVReader reader = new CSVReader(
				new InputStreamReader(new FileInputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
			
			String[] headers = reader.readNext();
			if (headers == null) throw new RuntimeException("CSV is empty");
			
			String[] line;
			while ((line = reader.readNext()) != null) {
				Map<String, String> row = new LinkedHashMap<>();
				for (int i = 0; i < headers.length; i++) {
					String key = keysLowerCase ? headers[i].trim().toLowerCase() : headers[i].trim();
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
	
	public static void display(List<Map<String, String>> csvData) {
		System.out.println("📋 ---- CSV Data ----");
		for (Map<String, String> row : csvData) {
			System.out.println("Row: " + row);
		}
		
		if (!csvData.isEmpty()) {
			Map<String, String> row = csvData.get(0);
			System.out.println("🔑 Keys: " + row.keySet());
			
			// Example field access
			if (row.containsKey("email")) System.out.println("📧 Email: " + row.get("email"));
			if (row.containsKey("state")) System.out.println("🏙️ State: " + row.get("state"));
			if (row.containsKey("country")) System.out.println("🌍 Country: " + row.get("country"));
			if (row.containsKey("phone")) System.out.println("📞 Phone: " + row.get("phone"));
		}
	}
}