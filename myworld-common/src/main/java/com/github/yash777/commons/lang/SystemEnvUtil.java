package com.github.yash777.commons.lang;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A utility class that provides helpful methods for retrieving system-level
 * environment properties, JVM details, and formatted environment variables.
 *
 * <p>Supports retrieving hostnames, user info, Java versions, heap stats,
 * and filtering environment variables.</p>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * System.out.println("Host: " + SystemEnvUtil.getHostName());
 * SystemEnvUtil.printMap(SystemEnvUtil.getSystemInfoAsMap(), "SYSTEM INFO");
 * }</pre>
 *
 * @author 🔐 Yash
 * @version 1.0
 */
public class SystemEnvUtil {
	
	public static void main(String[] args) {
		System.out.println("Host Name: " + getHostName());
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("Architecture: " + System.getProperty("os.arch"));
		System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("System DateTime: " + getSystemDateTime());
		
		printMap(getSystemInfoAsMap(), "SYSTEM INFO");
		printMap(getEnvVariablesContaining("java"), "ENV VARIABLES CONTAINING 'java'");
		printMap(getEnvVariablesContaining("temp"), "ENV VARIABLES CONTAINING 'temp'");
	}
	
	/**
	 * Retrieves the host name based on the OS environment variable.
	 * 
	 * <p>Uses {@code COMPUTERNAME} on Windows, and {@code HOSTNAME} on Unix-like systems.</p>
	 *
	 * @return the hostname if found, or {@code null} otherwise
	 */
	public static String getHostName() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains("win") ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
	}
	
	/**
	 * Returns the system's temporary directory path.
	 *
	 * @return the temp path from environment or system property fallback
	 */
	public static String getTempLocation() {
		String temp = System.getenv("TEMP");
		return (temp != null && !temp.isEmpty()) ? temp : System.getProperty("java.io.tmpdir");
	}
	
	/**
	 * Retrieves the current user name running the JVM.
	 *
	 * @return the user name as a string
	 */
	public static String getUser() {
		return System.getProperty("user.name");
	}
	
	/**
	 * Retrieves the current system date and time in {@code yyyy-MM-dd HH:mm:ss} format.
	 *
	 * @return formatted date-time string
	 */
	public static String getSystemDateTime() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(formatter);
	}
	
	/**
	 * Gathers the JVM heap memory usage as a map (max, total, free).
	 *
	 * @return a map with memory info in megabytes
	 */
	public static Map<String, String> getHeapInfoAsMap() {
		Runtime runtime = Runtime.getRuntime();
		Map<String, String> heapInfo = new HashMap<>();
		heapInfo.put("Max Heap Memory (MB)", String.valueOf(runtime.maxMemory() / (1024 * 1024)));
		heapInfo.put("Total Heap Memory (MB)", String.valueOf(runtime.totalMemory() / (1024 * 1024)));
		heapInfo.put("Free Heap Memory (MB)", String.valueOf(runtime.freeMemory() / (1024 * 1024)));
		return heapInfo;
	}
	
	/**
	 * Returns a high-level summary of system environment info.
	 *
	 * @return a linked map with user, temp, date, Java, and OS info
	 */
	public static Map<String, String> getSystemInfoAsMap() {
		Map<String, String> info = new LinkedHashMap<>();
		info.put("User", getUser());
		info.put("Temp Directory", getTempLocation());
		info.put("System DateTime", getSystemDateTime());
		info.put("Java Home", System.getProperty("java.home"));
		info.put("Java Version", System.getProperty("java.version"));
		info.put("OS", System.getProperty("os.name") + ", Version: " + System.getProperty("os.version") + ", Architecture: " + System.getProperty("os.arch"));
		info.put("Available Processors", String.valueOf(Runtime.getRuntime().availableProcessors()));
		info.putAll(getHeapInfoAsMap());
		return info;
	}
	
	/**
	 * Filters and returns environment variables where the key or value contains the given filter string (case-insensitive).
	 *
	 * @param filter the keyword to filter env vars
	 * @return a filtered map of matching environment variables
	 */
	public static Map<String, String> getEnvVariablesContaining(String filter) {
		Map<String, String> matches = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
			String key = entry.getKey().toLowerCase();
			String value = entry.getValue().toLowerCase();
			if (key.contains(filter.toLowerCase()) || value.contains(filter.toLowerCase())) {
				matches.put(entry.getKey(), entry.getValue());
			}
		}
		return matches;
	}
	
	/**
	 * Prints a map to the console with a section title.
	 *
	 * @param map the map to print
	 * @param title the title to print above the map
	 */
	public static void printMap(Map<String, String> map, String title) {
		System.out.println("\n====== " + title + " ======");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
