package com.github.yash777.commons.file;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class for file manipulation operations, including generating large test files
 * and splitting files into smaller chunks. This class provides methods to:
 * <ul>
 *     <li>Create dummy files of specified size</li>
 *     <li>Split large files into smaller parts</li>
 * </ul>
 * 
 * <h3>Example Usage:</h3>
 * <pre>
 *     // Create a 10MB dummy file
 *     FileSplitterUtility.createDummyFile("test.log", 10);
 *     
 *     // Split a file into 2MB chunks
 *     FileSplitterUtility.splitFile("large_file.log", "split_parts", 2);
 * </pre>
 * 
 * @author 🔐 Yash
 * @since 1.0
 */
public class FileSplitterUtility {
	
	/**
	 * Main method for demonstration purposes.
	 * Creates a dummy file and splits it into smaller chunks.
	 * 
	 * @param args Command line arguments (not used)
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		String tempPath = PathUtility.getTempDirectory().toString();
		System.out.println("System Temp Directory Path: " + tempPath);
		
		
		String inputFile = tempPath + "application.log"; // Input file path
		String outputDir = tempPath + "split_parts"; // Output directory - More readable and consistent
		
		// Uncomment to generate a 10MB dummy file
		createDummyFile(inputFile, 10);
		
		// Split the file into 2MB chunks (can be changed via parameter)
		splitFile(inputFile, outputDir, 2);
	}
	
	
	/**
	 * Creates a dummy file with the specified size in megabytes (MB).
	 * The file is filled with timestamped log lines for testing purposes.
	 * 
	 * @param filePath Path to the file to create
	 * @param sizeMB Desired size of the file in megabytes
	 * @throws IOException If an I/O error occurs while creating the file
	 */
	public static void createDummyFile(String filePath, int sizeMB) throws IOException {
		File file = new File(filePath);
		long targetSize = sizeMB * 1024L * 1024L; // Convert MB to bytes
		long currentSize = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		long currentTime = System.currentTimeMillis();
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			while (currentSize < targetSize) {
				String timestamp = sdf.format(new Date(currentTime));
				String line = timestamp + " INFO  This is a dummy log line for testing purposes.\n";
				writer.write(line);
				currentSize += line.getBytes().length;
				currentTime += 1000; // increment by 1 second
				
				writer.write(line);
				currentSize += line.getBytes().length;
			}
			System.out.println("Dummy file created with size: " + sizeMB + "MB");
		} catch (IOException e) {
			throw new IOException("Error while creating dummy file: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Splits the specified input file into smaller files of the given chunk size in megabytes (MB).
	 * Each part is saved in the specified output directory with a sequential numbering scheme.
	 * 
	 * @param inputFilePath Path to the file to be split
	 * @param outputDirPath Directory where split files will be saved
	 * @param chunkSizeMB Size of each chunk in megabytes
	 * @throws IOException If an I/O error occurs while splitting the file
	 */
	public static void splitFile(String inputFilePath, String outputDirPath, int chunkSizeMB) throws IOException {
		File inputFile = new File(inputFilePath);
		long chunkSize = chunkSizeMB * 1024L * 1024L; // Convert MB to bytes
		String baseName = inputFile.getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
		
		File outputDirectory = new File(outputDirPath);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}
		
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile))) {
			byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer
			int bytesRead;
			long bytesWritten = 0;
			int partCounter = 1;
			
			FileOutputStream fos = new FileOutputStream(new File(outputDirectory,
					baseName + "_part_" + partCounter + ".txt"));
			
			while ((bytesRead = bis.read(buffer)) != -1) {
				if (bytesWritten + bytesRead > chunkSize) {
					fos.close();
					partCounter++;
					fos = new FileOutputStream(new File(outputDirectory,
							baseName + "_part_" + partCounter + ".txt"));
					bytesWritten = 0;
				}
				fos.write(buffer, 0, bytesRead);
				bytesWritten += bytesRead;
			}
			
			fos.close();
			System.out.println("File split successfully into " + partCounter + " parts.");
		} catch (IOException e) {
			System.err.println("Error while splitting the file: " + e.getMessage());
		}
	}
}