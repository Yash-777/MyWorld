package com.github.yash777.commons.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for common file path operations using Java's {@code java.nio.file} package (NIO.2).
 * This class focuses on manipulating and querying path information, such as current directory,
 * parent/child paths, file extensions, and path normalization. It does NOT perform
 * file system operations like reading, writing, copying, moving, or deleting file contents.
 * For file I/O operations, refer to {@link FileIOUtil}.
 * 
 * @author 🔐 Yash
 */
public class PathUtility {
	
	/**
	 * Returns the canonical (absolute and normalized) path of the current working directory.
	 * This is typically the directory from which the Java application was launched.
	 *
	 * @return The canonical path of the current working directory.
	 * @throws IOException If an I/O error occurs while resolving the path.
	 */
	public static Path getCurrentProjectPath() throws IOException {
		//return (new File(".")).getCanonicalPath();
		return Paths.get(".").toRealPath();
	}
	
	/**
	 * Returns the canonical (absolute and normalized) path of the parent directory
	 * of the current working directory.
	 *
	 * @return The canonical path of the parent directory.
	 * @throws IOException If an I/O error occurs, or if the current project has no parent directory.
	 */
	public static Path getParentProjectPath() throws IOException {
		Path currentDir = getCurrentProjectPath();
		Path parentDir = currentDir.getParent();
		if (parentDir == null) {
			throw new IOException("Current project directory has no parent.");
		}
		return parentDir.toRealPath();
		//return (new File(".")).getCanonicalFile().getParentFile().getCanonicalPath();
	}
	
	/**
	 * Resolves a child path relative to the current working directory and returns its
	 * absolute and normalized form. This method does **not** require the child path to exist.
	 * It simply calculates what the full path would be.
	 *
	 * @param childPathString The string representation of the child path (e.g., "src/main/java", "temp").
	 * @return The absolute and normalized path of the specified child directory or file.
	 * @throws IOException If an I/O error occurs while resolving the current project path.
	 */
	public static Path getChildProjectPath(String childPathString) throws IOException {
		Path currentDir = getCurrentProjectPath();
		return currentDir.resolve(childPathString).normalize();
		//return (new File((new File(".")).getCanonicalPath(), childPathString)).getCanonicalPath()
	}
	
	/**
	 * Returns the user's home directory. This is a platform-independent way
	 * to get the home directory (e.g., C:\Users\YourUser on Windows, /home/youruser on Linux).
	 *
	 * @return The Path object representing the user's home directory.
	 */
	public static Path getUserHomeDirectory() {
		return Paths.get(System.getProperty("user.home"));
	}
	
	/**
	 * Returns the system's temporary directory. This is useful for creating
	 * temporary files or directories that can be cleaned up later.
	 *
	 * @return The Path object representing the system's temporary directory.
	 */
	public static Path getTempDirectory() {
		return Paths.get(System.getProperty("java.io.tmpdir"));
	}
	
	/**
	 * Checks if a given path exists on the file system.
	 *
	 * @param path The Path object to check.
	 * @return {@code true} if the path exists, {@code false} otherwise.
	 */
	public static boolean exists(Path path) {
		Objects.requireNonNull(path, "Path cannot be null");
		return Files.exists(path);
	}
	
	/**
	 * Checks if a given path is a regular file.
	 *
	 * @param path The Path object to check.
	 * @return {@code true} if the path exists and is a regular file, {@code false} otherwise.
	 */
	public static boolean isFile(Path path) {
		Objects.requireNonNull(path, "Path cannot be null");
		return Files.isRegularFile(path);
	}
	
	/**
	 * Checks if a given path is a directory.
	 *
	 * @param path The Path object to check.
	 * @return {@code true} if the path exists and is a directory, {@code false} otherwise.
	 */
	public static boolean isDirectory(Path path) {
		Objects.requireNonNull(path, "Path cannot be null");
		return Files.isDirectory(path);
	}
	
	/**
	 * Lists all files and directories directly within a given directory.
	 *
	 * @param directoryPath The path to the directory.
	 * @return A list of Path objects representing the entries in the directory.
	 * Returns an empty list if the directory does not exist or is empty.
	 * @throws IOException If an I/O error occurs while listing the directory.
	 */
	public static List<Path> listDirectoryContents(Path directoryPath) throws IOException {
		Objects.requireNonNull(directoryPath, "Directory path cannot be null");
		if (!Files.isDirectory(directoryPath)) {
			return Collections.emptyList();
		}
		try (Stream<Path> stream = Files.list(directoryPath)) {
			return stream.collect(Collectors.toList());
		}
	}
	
	/**
	 * Walks a file tree starting from a given directory and returns a list of all
	 * files and directories within it (including the starting directory itself).
	 *
	 * @param startPath The starting directory path.
	 * @return A list of Path objects representing all entries in the file tree.
	 * @throws IOException If an I/O error occurs during the file tree walk.
	 */
	public static List<Path> walkFileTree(Path startPath) throws IOException {
		Objects.requireNonNull(startPath, "Start path cannot be null");
		if (!Files.exists(startPath)) {
			return Collections.emptyList();
		}
		try (Stream<Path> stream = Files.walk(startPath)) {
			return stream.collect(Collectors.toList());
		}
	}
	
	/**
	 * Extracts the file extension from a given file path.
	 *
	 * @param filePath The Path object representing the file.
	 * @return The file extension (e.g., "txt", "jpg"), or an empty string if no extension is found.
	 * Returns an empty string if the path refers to a directory or has no file name.
	 */
	public static String getFileExtension(Path filePath) {
		Objects.requireNonNull(filePath, "File path cannot be null");
		Path fileName = filePath.getFileName();
		if (fileName == null) {
			return ""; // No file name component
		}
		String fileNameString = fileName.toString();
		int dotIndex = fileNameString.lastIndexOf('.');
		if (dotIndex > 0 && dotIndex < fileNameString.length() - 1) {
			return fileNameString.substring(dotIndex + 1);
		}
		return ""; // No extension or starts/ends with dot
	}
	
	/**
	 * Extracts the file name without its extension from a given file path.
	 *
	 * @param filePath The Path object representing the file.
	 * @return The file name without its extension (e.g., "document", "image"),
	 * or the full file name if no extension is found.
	 * Returns an empty string if the path refers to a directory or has no file name.
	 */
	public static String getFileNameWithoutExtension(Path filePath) {
		Objects.requireNonNull(filePath, "File path cannot be null");
		Path fileName = filePath.getFileName();
		if (fileName == null) {
			return ""; // No file name component
		}
		String fileNameString = fileName.toString();
		int dotIndex = fileNameString.lastIndexOf('.');
		if (dotIndex > 0) {
			return fileNameString.substring(0, dotIndex);
		}
		return fileNameString; // No extension
	}
	
	/**
	 * Normalizes a given path by removing redundant elements such as "." and "..".
	 * For example, "/a/./b/../c" would be normalized to "/a/c".
	 *
	 * @param path The Path object to normalize.
	 * @return The normalized Path object.
	 */
	public static Path normalizePath(Path path) {
		Objects.requireNonNull(path, "Path cannot be null");
		return path.normalize();
	}
	
	/**
	 * Resolves a path against a base path. If the {@code other} path is absolute,
	 * this method simply returns {@code other}. Otherwise, it resolves {@code other}
	 * against {@code basePath}.
	 *
	 * @param basePath The base path.
	 * @param other The path to resolve.
	 * @return The resolved path.
	 */
	public static Path resolvePath(Path basePath, Path other) {
		Objects.requireNonNull(basePath, "Base path cannot be null");
		Objects.requireNonNull(other, "Other path cannot be null");
		return basePath.resolve(other);
	}
	
	/**
	 * Constructs a relative path between two paths.
	 * For example, relativize(Paths.get("/a/b"), Paths.get("/a/b/c/d")) would return "c/d".
	 *
	 * @param originalPath The original path.
	 * @param otherPath The path to relativize against the original path.
	 * @return The relative path.
	 */
	public static Path relativizePath(Path originalPath, Path otherPath) {
		Objects.requireNonNull(originalPath, "Original path cannot be null");
		Objects.requireNonNull(otherPath, "Other path cannot be null");
		return originalPath.relativize(otherPath);
	}
	
	// --- Main method for examples (now uses FileIOUtil for file operations) ---
	public static void main(String[] args) {
		try {
			System.out.println("--- Path Information ---");
			System.out.println("Current Project Path: " + getCurrentProjectPath());
			System.out.println("Parent Project Path: " + getParentProjectPath());
			System.out.println("User Home Directory: " + getUserHomeDirectory());
			System.out.println("Temporary Directory: " + getTempDirectory());
			
			Path childTempPath = getChildProjectPath("temp");
			System.out.println("Child 'temp' Folder Path (resolved, not necessarily existing): " + childTempPath);
			
			
			Path exampleDir = Paths.get("example_data");
			Path exampleFile = exampleDir.resolve("my_document.txt");
			
			System.out.println("\n--- Path & Existence Checks (PathUtility) ---");
			System.out.println("Does " + exampleDir + " exist? " + PathUtility.exists(exampleDir));
			System.out.println("Is " + exampleDir + " a directory? " + PathUtility.isDirectory(exampleDir));
			System.out.println("Does " + exampleFile + " exist? " + PathUtility.exists(exampleFile));
			System.out.println("Is " + exampleFile + " a file? " + PathUtility.isFile(exampleFile));
			
			
			System.out.println("\n--- Path Manipulation (PathUtility) ---");
			// Get File Extension and Name Without Extension
			Path complexPath = Paths.get("/path/to/archive.tar.gz");
			System.out.println("File name for " + exampleFile + ": " + exampleFile.getFileName());
			System.out.println("Extension for " + exampleFile + ": " + PathUtility.getFileExtension(exampleFile));
			System.out.println("Name without extension for " + exampleFile + ": " + PathUtility.getFileNameWithoutExtension(exampleFile));
			System.out.println("Extension for " + complexPath + ": " + PathUtility.getFileExtension(complexPath));
			System.out.println("Name without extension for " + complexPath + ": " + PathUtility.getFileNameWithoutExtension(complexPath));
			
			// Normalize Path
			Path messyPath = Paths.get("/home/user/./documents/../project/./file.txt");
			System.out.println("\nMessy path: " + messyPath);
			System.out.println("Normalized path: " + PathUtility.normalizePath(messyPath));
			
			// Resolve Path
			Path base = Paths.get("/usr/local");
			Path relative = Paths.get("bin/app");
			Path absolute = Paths.get("/opt/software");
			System.out.println("\nResolving " + relative + " against " + base + ": " + PathUtility.resolvePath(base, relative));
			System.out.println("Resolving " + absolute + " against " + base + ": " + PathUtility.resolvePath(base, absolute)); // Absolute path wins
			
			// Relativize Path
			Path path1 = Paths.get("/a/b/c");
			Path path2 = Paths.get("/a/b/x/y");
			System.out.println("\nRelativizing " + path2 + " from " + path1 + ": " + PathUtility.relativizePath(path1, path2));
			
			System.out.println("\n--- Demonstrating File I/O (using FileIOUtil) ---");
			// Example of creating and deleting using FileIOUtil
			FileIOUtil.createDirectory(exampleDir);
			FileIOUtil.createFile(exampleFile);
			FileIOUtil.writeLines(exampleFile, List.of("Line 1", "Line 2"));
			System.out.println("Content of " + exampleFile + ": " + FileIOUtil.readAllLines(exampleFile));
			
			
		} catch (IOException e) {
			System.err.println("An I/O error occurred: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Clean up created directories and files for testing using FileIOUtil
			try {
				Path exampleDir = Paths.get("example_data");
				Path movedFile = Paths.get("moved_document.txt");
				if (PathUtility.exists(exampleDir)) { // Still use PathUtility for existence check
					System.out.println("\nCleaning up example_data directory...");
					FileIOUtil.deleteRecursively(exampleDir); // Now uses FileIOUtil
					System.out.println("Does " + exampleDir + " exist after cleanup? " + PathUtility.exists(exampleDir));
				}
				if (PathUtility.exists(movedFile)) { // Still use PathUtility for existence check
					System.out.println("Cleaning up moved_document.txt...");
					FileIOUtil.delete(movedFile); // Now uses FileIOUtil
					System.out.println("Does " + movedFile + " exist after cleanup? " + PathUtility.exists(movedFile));
				}
			} catch (IOException e) {
				System.err.println("Error during cleanup: " + e.getMessage());
			}
		}
	}
}