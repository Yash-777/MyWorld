package com.github.yash777.commons.file;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for writing data to CSV files using OpenCSV.
 * 
 * Helpful converters:
 * - https://codebeautify.org/csv-to-excel-converter
 * - https://tableconvert.com/csv-to-excel
 */
public class CsvFileUtil2 {
	
	/**
	 * Writes a list of rows to a CSV file.
	 *
	 * @param filePath Path to the CSV file.
	 * @param data     List of rows to write (each row is a String array).
	 * @param append   If true, data is appended; if false, file is overwritten.
	 * @throws IOException If file writing fails.
	 */
	public static void writeCsv(String filePath, List<String[]> data, boolean append) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, append))) {
			writeCsv(writer, data);
		}
	}
	
	/**
	 * Writes a list of rows to an already open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param data   List of rows to write (each row is a String array).
	 */
	public static void writeCsv(CSVWriter writer, List<String[]> data) {
		if (data != null) {
			for (String[] row : data) {
				writer.writeNext(row);
			}
		}
	}
	
	/**
	 * Writes a header row to the CSV file (overwrites the file).
	 *
	 * @param filePath Path to the CSV file.
	 * @param header   Header row as a String array.
	 * @throws IOException If file writing fails.
	 */
	public static void writeHeader(String filePath, String[] header) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, false))) {
			writeHeader(writer, header);
		}
	}
	
	/**
	 * Writes a header row using an open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param header Header row as a String array.
	 */
	public static void writeHeader(CSVWriter writer, String[] header) {
		if (header != null) {
			writer.writeNext(header);
		}
	}
	
	/**
	 * Appends a single row to the CSV file.
	 *
	 * @param filePath Path to the CSV file.
	 * @param row      Row to append as a String array.
	 * @throws IOException If file writing fails.
	 */
	public static void appendRow(String filePath, String[] row) throws IOException {
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
			appendRow(writer, row);
		}
	}
	
	/**
	 * Appends a single row using an open CSVWriter.
	 *
	 * @param writer Open CSVWriter instance.
	 * @param row    Row to append as a String array.
	 */
	public static void appendRow(CSVWriter writer, String[] row) {
		if (row != null) {
			writer.writeNext(row);
		}
	}
	
	
    /**
     * Writes data to a CSV file given a list of headers and map-based rows.
     *
     * @param filePath Path to the CSV file.
     * @param headers  Ordered list of header keys.
     * @param data     List of map entries representing rows.
     * @param append   If true, appends to the file; otherwise, overwrites.
     * @throws IOException If writing fails.
     */
    public static void writeCsv(String filePath, List<String> headers, List<Map<String, String>> data, boolean append) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, append))) {
            writeCsv(writer, headers, data);
        }
    }

    /**
     * Writes data to a CSV file using an open CSVWriter.
     *
     * @param writer  Open CSVWriter instance.
     * @param headers Ordered list of header keys.
     * @param data    List of map entries representing rows.
     */
    public static void writeCsv(CSVWriter writer, List<String> headers, List<Map<String, String>> data) {
        if (headers == null || headers.isEmpty()) return;

        // Write headers (if writer is at the beginning of file, assume header needs writing)
        writer.writeNext(headers.toArray(new String[0]));

        // Write data rows
        for (Map<String, String> rowMap : data) {
            String[] row = new String[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                String key = headers.get(i);
                row[i] = rowMap.getOrDefault(key, ""); // Fill blank if key missing
            }
            writer.writeNext(row);
        }
    }
    
	/**
	 * Demonstrates the usage of CsvFileUtil methods.
	 *
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		String filePath = "people.csv";
		
		// Define headers in specific order
	    List<String> headers = List.of("ID", "Name", "Age", "City");

		// ✅ Example 1: Using try-with-resources -> Write header and rows
		try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, false))) {
			writeHeader(writer, new String[]{"ID", "Name", "Age"});
			writeCsv(writer, List.of(
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
			writeCsv(filePath, List.of(
					new String[]{"5", "Eva", "29"},
					new String[]{"6", "Frank", "33"}
					), true);
			appendRow(filePath, new String[]{"7", "Grace", "26"});
		} catch (IOException e) {
			System.err.println("Error in direct utility usage: " + e.getMessage());
		}
		
		
		// Define data as a list of maps (unordered key/value pairs)
	    List<Map<String, String>> people = new ArrayList<>();

	    Map<String, String> row1 = new HashMap<>();
	    row1.put("Name", "Alice");
	    row1.put("ID", "1");
	    row1.put("Age", "30");
	    row1.put("City", "New York");

	    Map<String, String> row2 = new HashMap<>();
	    row2.put("Name", "Bob");
	    row2.put("City", "Los Angeles");
	    row2.put("Age", "25");
	    row2.put("ID", "2");

	    people.add(row1);
	    people.add(row2);

	    try {
	        // Overwrites existing file and writes ordered CSV
	        writeCsv(filePath, headers, people, false);
	        System.out.println("Dynamic CSV written successfully.");
	    } catch (IOException e) {
	        System.err.println("Failed to write CSV: " + e.getMessage());
	    }
	    
		System.out.println("CSV writing completed.");
	}
}
