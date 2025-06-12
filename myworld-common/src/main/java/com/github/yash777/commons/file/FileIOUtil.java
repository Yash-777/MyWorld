package com.github.yash777.commons.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;

/**
 * A utility class for common file system input/output (I/O) operations using Java's
 * {@code java.nio.file} package (NIO.2). This class provides methods for
 * creating, deleting, copying, moving, and reading/writing files and directories.
 * For path manipulation and querying path information, refer to {@link PathUtility}.
 * 
 * @author 🔐 Yash
 */
public class FileIOUtil {
	
	/**
	 * Creates a new directory at the specified path. If parent directories do not exist,
	 * they will also be created. This method does not throw an exception if the directory
	 * already exists.
	 *
	 * @param directoryPath The Path object representing the directory to create.
	 * @throws IOException If an I/O error occurs during directory creation (e.g., permissions).
	 */
	public static void createDirectory(Path directoryPath) throws IOException {
		Objects.requireNonNull(directoryPath, "Directory path cannot be null");
		Files.createDirectories(directoryPath);
	}
	
	/**
	 * Creates a new empty file at the specified path. If parent directories do not exist,
	 * they will be created. This method throws a {@code FileAlreadyExistsException}
	 * if the file already exists.
	 *
	 * @param filePath The Path object representing the file to create.
	 * @throws IOException If an I/O error occurs, or if the file already exists.
	 */
	public static void createFile(Path filePath) throws IOException {
		Objects.requireNonNull(filePath, "File path cannot be null");
		Files.createDirectories(filePath.getParent()); // Ensure parent directories exist
		Files.createFile(filePath);
	}
	
	/**
	 * Deletes a file or an empty directory.
	 *
	 * @param path The Path object of the file or empty directory to delete.
	 * @return {@code true} if the path was successfully deleted, {@code false} otherwise.
	 * @throws IOException If an I/O error occurs, or if the directory is not empty.
	 */
	public static boolean delete(Path path) throws IOException {
		Objects.requireNonNull(path, "Path cannot be null");
		return Files.deleteIfExists(path); // Returns true if deleted, false if not found
	}
	
	/**
	 * Deletes a file or directory recursively. For directories, all contents
	 * (files and subdirectories) will be deleted.
	 *
	 * @param path The Path object of the file or directory to delete.
	 * @throws IOException If an I/O error occurs during deletion.
	 */
	public static void deleteRecursively(Path path) throws IOException {
		Objects.requireNonNull(path, "Path cannot be null");
		if (!Files.exists(path)) {
			return; // Nothing to delete
		}
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc != null) {
					throw exc;
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	/**
	 * Copies a file from the source path to the target path.
	 * Existing files at the target will be replaced.
	 *
	 * @param source The source file path.
	 * @param target The target file path.
	 * @throws IOException If an I/O error occurs during the copy operation.
	 */
	public static void copyFile(Path source, Path target) throws IOException {
		Objects.requireNonNull(source, "Source path cannot be null");
		Objects.requireNonNull(target, "Target path cannot be null");
		Files.createDirectories(target.getParent()); // Ensure target directory exists
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Moves a file or directory from the source path to the target path.
	 * Existing files at the target will be replaced.
	 *
	 * @param source The source file or directory path.
	 * @param target The target file or directory path.
	 * @throws IOException If an I/O error occurs during the move operation.
	 */
	public static void move(Path source, Path target) throws IOException {
		Objects.requireNonNull(source, "Source path cannot be null");
		Objects.requireNonNull(target, "Target path cannot be null");
		Files.createDirectories(target.getParent()); // Ensure target directory exists
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Reads all bytes from a file into a byte array.
	 *
	 * @param filePath The path to the file.
	 * @return A byte array containing the contents of the file.
	 * @throws IOException If an I/O error occurs reading from the file.
	 */
	public static byte[] readAllBytes(Path filePath) throws IOException {
		Objects.requireNonNull(filePath, "File path cannot be null");
		return Files.readAllBytes(filePath);
	}
	
	/**
	 * Writes bytes to a file. If the file already exists, its contents are truncated.
	 * If parent directories do not exist, they will be created.
	 *
	 * @param filePath The path to the file.
	 * @param bytes The byte array to write to the file.
	 * @throws IOException If an I/O error occurs writing to the file.
	 */
	public static void writeBytes(Path filePath, byte[] bytes) throws IOException {
		Objects.requireNonNull(filePath, "File path cannot be null");
		Objects.requireNonNull(bytes, "Bytes to write cannot be null");
		Files.createDirectories(filePath.getParent()); // Ensure parent directories exist
		Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	/**
	 * Reads all lines from a text file into a list of strings.
	 *
	 * @param filePath The path to the text file.
	 * @return A list of strings, where each string is a line from the file.
	 * @throws IOException If an I/O error occurs reading from the file.
	 */
	public static List<String> readAllLines(Path filePath) throws IOException {
		Objects.requireNonNull(filePath, "File path cannot be null");
		return Files.readAllLines(filePath);
	}
	
	/**
	 * Writes a list of strings to a text file, one line per string.
	 * If the file already exists, its contents are truncated.
	 * If parent directories do not exist, they will be created.
	 *
	 * @param filePath The path to the text file.
	 * @param lines The list of strings to write.
	 * @throws IOException If an I/O error occurs writing to the file.
	 */
	public static void writeLines(Path filePath, List<String> lines) throws IOException {
		Objects.requireNonNull(filePath, "File path cannot be null");
		Objects.requireNonNull(lines, "Lines to write cannot be null");
		Files.createDirectories(filePath.getParent()); // Ensure parent directories exist
		Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	// --- Main method for examples ---
	public static void main(String[] args) {
		Path exampleDir = Paths.get("file_io_example_data");
		Path exampleFile = exampleDir.resolve("sample.txt");
		Path copiedFile = exampleDir.resolve("sample_copy.txt");
		Path movedFile = exampleDir.resolve("renamed.txt");
		
		try {
			System.out.println("--- File I/O Operations (FileIOUtil) ---");
			
			// Create Directory
			System.out.println("\nCreating directory: " + exampleDir);
			FileIOUtil.createDirectory(exampleDir);
			System.out.println("Directory exists: " + Files.exists(exampleDir)); // Using Files.exists directly for verification
			
			// Create File
			System.out.println("\nCreating file: " + exampleFile);
			FileIOUtil.createFile(exampleFile);
			System.out.println("File exists: " + Files.exists(exampleFile));
			
			// Write Lines
			List<String> lines = List.of("First line of text.", "Second line with more content.");
			System.out.println("\nWriting lines to " + exampleFile);
			FileIOUtil.writeLines(exampleFile, lines);
			System.out.println("Content written.");
			
			// Read Lines
			System.out.println("\nReading lines from " + exampleFile);
			List<String> readLines = FileIOUtil.readAllLines(exampleFile);
			readLines.forEach(System.out::println);
			
			// Write Bytes
			String byteContent = "This is some binary data.";
			Path binaryFile = exampleDir.resolve("binary.bin");
			System.out.println("\nWriting bytes to " + binaryFile);
			FileIOUtil.writeBytes(binaryFile, byteContent.getBytes());
			System.out.println("Bytes written.");
			
			// Read Bytes
			System.out.println("\nReading bytes from " + binaryFile);
			byte[] readBytes = FileIOUtil.readAllBytes(binaryFile);
			System.out.println("Content read: " + new String(readBytes));
			
			// Copy File
			System.out.println("\nCopying " + exampleFile + " to " + copiedFile);
			FileIOUtil.copyFile(exampleFile, copiedFile);
			System.out.println("Copied file exists: " + Files.exists(copiedFile));
			System.out.println("Content of copied file: " + FileIOUtil.readAllLines(copiedFile));
			
			// Move File
			System.out.println("\nMoving " + copiedFile + " to " + movedFile);
			FileIOUtil.move(copiedFile, movedFile);
			System.out.println("Original copied file exists: " + Files.exists(copiedFile)); // Should be false
			System.out.println("Moved file exists: " + Files.exists(movedFile)); // Should be true
			
			// Delete single file
			System.out.println("\nDeleting " + movedFile);
			boolean deleted = FileIOUtil.delete(movedFile);
			System.out.println("File deleted: " + deleted);
			System.out.println("Moved file exists: " + Files.exists(movedFile)); // Should be false
			
			
		} catch (IOException e) {
			System.err.println("An I/O error occurred: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Clean up created directories and files
			try {
				if (Files.exists(exampleDir)) {
					System.out.println("\nCleaning up example directory: " + exampleDir + " recursively...");
					FileIOUtil.deleteRecursively(exampleDir);
					System.out.println("Directory exists after cleanup: " + Files.exists(exampleDir));
				}
			} catch (IOException e) {
				System.err.println("Error during cleanup: " + e.getMessage());
			}
		}
	}
}