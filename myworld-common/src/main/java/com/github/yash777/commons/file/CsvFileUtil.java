package com.github.yash777.commons.file;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import lombok.*;

/*
<!-- https://mvnrepository.com/artifact/com.opencsv/opencsv -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.12.0</version>
</dependency>
 */
/**
 * Utility class for writing data to CSV files using OpenCSV.
 * <p>
 * Features:
 * - Checks if headers already exist in a given CSV file and avoids rewriting headers.
 * - Supports multiple CSV files concurrently with independent header tracking.
 * - Supports bulk writing/appending for both map-based and array-based data.
 * - Provides methods accepting either a file path or an open CSVWriter.
 * <p>
 * Usage example:
 * <pre>
 * List<String> headers = List.of("ID", "Name", "Age");
 * List<Map<String, String>> data = List.of(
 *     Map.of("ID", "1", "Name", "Alice", "Age", "30"),
 *     Map.of("ID", "2", "Name", "Bob", "Age", "25")
 * );
 * CsvFileUtil.writeRows("file1.csv", headers, data, false); // overwrites
 * CsvFileUtil.appendCsv("file1.csv", headers, Map.of("ID", "3", "Name", "Charlie", "Age", "40")); // append
 * </pre>
 */
public class CsvFileUtil {
	
	// Tracks whether headers have been written & matched for each file path
	private static final Map<String, Boolean> headersMatchedMap = new HashMap<>();
	
	/**
	 * Bulk writes map-based data to a CSV file, writing headers if needed.
	 *
	 * @param filePath Path to the CSV file.
	 * @param headers  Ordered list of header keys.
	 * @param data     List of map entries representing rows.
	 * @param append   If true, append to file; else overwrite.
	 * @throws IOException if file operations fail.
	 * @throws CsvValidationException 
	 */
	public static void writeRecords(String filePath, List<String> headers,
			List<Map<String, String>> data, boolean append) throws IOException, CsvValidationException {
		ensureHeadersWritten(headers, filePath, append);
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, append))) {
			writeRecords(writer, headers, data, filePath);
		}
	}
	
	/**
	 * Bulk writes map-based data using an open CSVWriter, writing headers if needed.
	 *
	 * @param writer   Open CSVWriter instance.
	 * @param headers  Ordered list of header keys.
	 * @param data     List of map entries representing rows.
	 * @param filePath File path for header tracking (used internally).
	 */
	public static void writeRecords(CSVWriter writer, List<String> headers,
			List<Map<String, String>> data, String filePath) {
		if (!headersMatchedMap.getOrDefault(filePath, false)) {
			writer.writeNext(headers.toArray(new String[0]));
			headersMatchedMap.put(filePath, true);
		}
		for (Map<String, String> rowMap : data) {
			String[] row = headers.stream()
					.map(h -> rowMap.getOrDefault(h, ""))
					.toArray(String[]::new);
			writer.writeNext(row);
		}
	}
	
	/**
	 * Appends a single map-based row to the CSV file, writing headers if needed.
	 *
	 * @param filePath Path to the CSV file.
	 * @param headers  Ordered list of header keys.
	 * @param rowMap   Map representing a single row.
	 * @throws IOException if file operations fail.
	 * @throws CsvValidationException 
	 */
	public static void appendRecord(String filePath, List<String> headers,
			Map<String, String> rowMap) throws IOException, CsvValidationException {
		ensureHeadersWritten(headers, filePath, true);
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
			appendRecord(writer, headers, rowMap, filePath);
		}
	}
	
	/**
	 * Appends a single map-based row using an open CSVWriter, writing headers if needed.
	 *
	 * @param writer   Open CSVWriter instance.
	 * @param headers  Ordered list of header keys.
	 * @param rowMap   Map representing a single row.
	 * @param filePath File path for header tracking (used internally).
	 */
	public static void appendRecord(CSVWriter writer, List<String> headers,
			Map<String, String> rowMap, String filePath) {
		if (!headersMatchedMap.getOrDefault(filePath, false)) {
			writer.writeNext(headers.toArray(new String[0]));
			headersMatchedMap.put(filePath, true);
		}
		String[] row = headers.stream()
				.map(h -> rowMap.getOrDefault(h, ""))
				.toArray(String[]::new);
		writer.writeNext(row);
	}
	
	/**
	 * Bulk writes array-based data to CSV file.
	 *
	 * @param filePath Path to the CSV file.
	 * @param data     List of String array rows.
	 * @param append   Append mode if true, else overwrite.
	 * @throws IOException if file operations fail.
	 */
	public static void writeRows(String filePath, List<String[]> data, boolean append) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, append))) {
			writeRows(writer, data);
		}
	}
	
	/**
	 * Bulk writes array-based data using an open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param data   List of String array rows.
	 */
	public static void writeRows(CSVWriter writer, List<String[]> data) {
		for (String[] row : data) {
			writer.writeNext(row);
		}
	}
	
	/**
	 * Appends a single array-based row to a CSV file.
	 *
	 * @param filePath Path to the CSV file.
	 * @param row      String array row to append.
	 * @throws IOException if file operations fail.
	 */
	public static void appendRow(String filePath, String[] row) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
			appendRow(writer, row);
		}
	}
	
	/**
	 * Appends a single array-based row using an open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param row    String array row to append.
	 */
	public static void appendRow(CSVWriter writer, String[] row) {
		writer.writeNext(row);
	}
	
	/**
	 * Writes a header row to a CSV file, overwriting existing file.
	 * Throws IOException if the file already contains data.
	 *
	 * @param filePath Path to the CSV file.
	 * @param header   String array header row.
	 * @throws IOException If the file already contains data or can't be written to.
	 */
	public static void writeHeader(String filePath, String[] header) throws IOException {
		Path path = Path.of(filePath);
		File file = path.toFile();
		
		if (file.exists() && file.length() > 0) {
			throw new IOException("File already contains data or headers. Please clear the file before writing headers.");
		}
		
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, false))) {
			writeHeader(writer, header);
		}
	}
	
	/**
	 * Writes a header row to a CSV file, overwriting existing file.
	 * Accepts List of strings as headers.
	 *
	 * @param filePath Path to the CSV file.
	 * @param headers  List of header strings.
	 * @throws IOException If the file already contains data or can't be written to.
	 */
	public static void writeHeader(String filePath, List<String> headers) throws IOException {
		writeHeader(filePath, headers.toArray(new String[0]));
	}
	
	/**
	 * Writes a header row using an open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param header String array header row.
	 */
	public static void writeHeader(CSVWriter writer, String[] header) {
		if (header != null && header.length > 0) {
			writer.writeNext(header);
		}
	}
	
	/**
	 * Writes a header row using an open CSVWriter.
	 *
	 * @param writer  Open CSVWriter instance.
	 * @param headers List of header strings.
	 */
	public static void writeHeader(CSVWriter writer, List<String> headers) {
		writeHeader(writer, headers.toArray(new String[0]));
	}
	
	/**
	 * Checks if headers already exist in the CSV file and sets headersMatchedMap accordingly.
	 * Resets to false if file is overwritten.
	 *
	 * @param headers  Ordered list of headers.
	 * @param filePath File path of CSV.
	 * @param append   Append mode flag.
	 * @throws IOException if file operations fail.
	 * @throws CsvValidationException 
	 */
	private static void ensureHeadersWritten(List<String> headers, String filePath,
			boolean append) throws IOException, CsvValidationException {
		if (!append) {
			// Overwrite mode: reset tracking
			headersMatchedMap.put(filePath, false);
			return;
		}
		// Append mode: check if file exists and headers match
		Path p = Path.of(filePath);
		if (Files.exists(p)) {
			try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
				String[] existingHeader = reader.readNext();
				if (existingHeader != null && Arrays.equals(existingHeader, headers.toArray(new String[0]))) {
					headersMatchedMap.put(filePath, true);
				} else {
					headersMatchedMap.put(filePath, false);
				}
			}
		} else {
			// File doesn't exist, so header not matched yet
			headersMatchedMap.put(filePath, false);
		}
	}
	
	/**
	 * Example main method demonstrating writing/appending to multiple CSV files
	 * while maintaining independent header tracking per file.
	 *
	 * @param args Command-line arguments (unused).
	 * @throws IOException if file operations fail.
	 * @throws CsvValidationException 
	 */
	public static void main(String[] args) throws Exception {
		String filePath = "D:/people.csv";
		/*
		 * ✅ Array-based methods (raw rows)
		writeCsv(...)	writeRows(...)	Raw CSV rows = unstructured.
		appendCsv(...)	appendRow(...)	Appending one raw row.
		 */
		
		// Define headers in specific order
		List<String> headers = List.of("ID", "Name", "Age", "City");
		
		// ✅ Example 1: Using try-with-resources -> Write header and rows
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, false))) {
			writeHeader(writer, new String[]{"ID", "Name", "Age"});
			writeRows(writer, List.of(
					new String[]{"1", "Alice", "30"},
					new String[]{"2", "Bob", "25"}
					));
		} catch (IOException e) {
			System.err.println("Error in try-with-resources block: " + e.getMessage());
		}
		
		// ✅ Example 2: Using try-catch-finally (manual flush & close)
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(filePath, true)); // append = true
			appendRow(writer, new String[]{"3", "Charlie", "40"});
			appendRow(writer, new String[]{"4", "Diana", "22"});
			writer.flush(); // ensure all buffered data is written to file
		} catch (IOException e) {
			System.err.println("Error in manual try-catch-finally block: " + e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.err.println("Error closing writer: " + e.getMessage());
				}
			}
		}
		
		// ✅ Example 3: Re-using utility methods with file path
		try {
			writeRows(filePath, List.of(
					new String[]{"5", "Eva", "29"},
					new String[]{"6", "Frank", "33"}
					), true);
			appendRow(filePath, new String[]{"7", "Grace", "26"});
		} catch (IOException e) {
			System.err.println("Error in direct utility usage: " + e.getMessage());
		}
		
		/*
		 * ✅ Map-based methods (structured records)
		writeCsv(...)	writeRecords(...)	Reflects writing multiple structured records.
		appendCsv(...)	appendRecord(...)	Appending a single structured record.
		 */
		String filePath1 = "D:/file1.csv";
		String filePath2 = "D:/file2.csv";
		// Prepare sample data for file1
		List<Map<String, String>> dataFile1 = List.of(
				Map.of("ID", "1", "Name", "Alice", "Age", "30"),
				Map.of("ID", "2", "Name", "Bob", "Age", "25")
				);
		
		// Prepare sample data for file2
		List<Map<String, String>> dataFile2 = List.of(
				Map.of("ID", "101", "Name", "Xavier", "Age", "45"),
				Map.of("ID", "102", "Name", "Yara", "Age", "38")
				);
		
		// Write file1 (Overwrites existing file and writes ordered CSV)
		writeRecords(filePath1, headers, dataFile1, false);
		
		// Write file2 (overwrite)
		writeRecords(filePath2, headers, dataFile2, false);
		
		// Append to file1
		appendRecord(filePath1, headers, Map.of("ID", "3", "Name", "Charlie", "Age", "40"));
		
		// Append to file2
		appendRecord(filePath2, headers, Map.of("ID", "103", "Name", "Zoe", "Age", "29"));
		
		System.out.println("CSV files written and appended successfully.");
		
		
		// Method 1: Raw lists
		CsvFileUtil.Pair<List<String>, List<List<String>>> dataLists = CsvFileUtil.readCsvAsLists(filePath);
		System.out.println("Headers: " + dataLists.getHeaders());
		System.out.println("Rows:");
		for (List<String> row : dataLists.getRecords()) {
			System.out.println(row);
		}
		
		// Method 2: Map-based
		CsvFileUtil.Pair<List<String>, List<Map<String, String>>> dataMaps = CsvFileUtil.readCsvAsMaps(filePath1);
		System.out.println("\nHeaders: " + dataMaps.getHeaders());
		System.out.println("Rows as maps:");
		for (Map<String, String> rowMap : dataMaps.getRecords()) {
			System.out.println(rowMap);
		}
	}
	
	
	
	// Read
	/**
	 * Reads CSV file into headers and raw rows (list of lists).
	 *
	 * @param filePath CSV file path.
	 * @return A pair of headers and rows (each row is a list of strings).
	 * @throws IOException if reading fails.
	 * @throws CsvException 
	 */
	public static Pair<List<String>, List<List<String>>> readCsvAsLists(String filePath) throws IOException, CsvException {
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			List<String[]> allRows = reader.readAll();
			
			if (allRows.isEmpty()) {
				return new Pair<>(Collections.emptyList(), Collections.emptyList());
			}
			
			// First row is header
			List<String> headers = Arrays.asList(allRows.get(0));
			List<List<String>> records = new ArrayList<>();
			
			// Remaining rows are data
			for (int i = 1; i < allRows.size(); i++) {
				records.add(Arrays.asList(allRows.get(i)));
			}
			
			return new Pair<>(headers, records);
		}
	}
	
	/**
	 * Reads CSV file into headers and records as maps.
	 *
	 * @param filePath CSV file path.
	 * @return A pair of headers and rows (each row is a map from header to value).
	 * @throws IOException if reading fails.
	 * @throws CsvException 
	 */
	public static Pair<List<String>, List<Map<String, String>>> readCsvAsMaps(String filePath) throws IOException, CsvException {
		try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
			List<String[]> allRows = reader.readAll();
			
			if (allRows.isEmpty()) {
				return new Pair<>(Collections.emptyList(), Collections.emptyList());
			}
			
			// First row is header
			List<String> headers = Arrays.asList(allRows.get(0));
			List<Map<String, String>> records = new ArrayList<>();
			
			// Remaining rows are data
			for (int i = 1; i < allRows.size(); i++) {
				String[] row = allRows.get(i);
				Map<String, String> map = new LinkedHashMap<>();
				for (int j = 0; j < headers.size(); j++) {
					String value = j < row.length ? row[j] : "";
					map.put(headers.get(j), value);
				}
				records.add(map);
			}
			
			return new Pair<>(headers, records);
		}
	}
	
	// Simple Pair class for returning two related objects : POJO to hold headers + records
	@lombok.Data @AllArgsConstructor @NoArgsConstructor
	public static class Pair<K, V> {
		private K headers;
		private V records;
		
	}
}
