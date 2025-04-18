package com.github.yash777;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A utility class to determine the origin of a loaded class, specifically
 * the JAR file or module in which the class is packaged.
 *
 * <p>
 * Useful for:
 * <ul>
 *     <li>Debugging class loading issues</li>
 *     <li>Understanding third-party and system class dependencies</li>
 *     <li>Verifying Maven-resolved JAR locations</li>
 * </ul>
 * </p>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * // Check where a class is loaded from
 * String location = FindClassBelongsToJar.findJarFile(String.class);
 * System.out.println("Class Location: " + location);
 *
 * // Get Maven-specific path info if available
 * Map<String, String> details = FindClassBelongsToJar.findJarDetails(StringUtils.class);
 * details.forEach((k, v) -> System.out.println(k + ": " + v));
 * }</pre>
 *
 * @author 🔐 Yash
 * @since 1.0
 */
public class FindClassBelongsToJar {
	
	/**
	 * Entry point to demonstrate how the utility works with a few well-known classes.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		Class<?> jdkClass = java.lang.String.class;
		System.out.println("JDK Class Location: " + findJarFile(jdkClass));
		System.out.println("Details: " + findJarDetails(jdkClass));
		
		Class<?> apacheClass = org.apache.commons.lang3.StringUtils.class;
		System.out.println("\nThird-Party Class (Apache): " + findJarFile(apacheClass));
		System.out.println("Details: " + findJarDetails(apacheClass));
		
		Class<?> javaxClass = javax.annotation.PostConstruct.class;
		System.out.println("\nJava EE Class: " + findJarFile(javaxClass));
		System.out.println("Details: " + findJarDetails(javaxClass));
		
		Class<?> moduleClass = com.github.yash777.commons.lang.StringUtils.class;
		System.out.println("\nMyModule Application Class: " + findJarFile(moduleClass));
		System.out.println("Details: " + findJarDetails(moduleClass));
	}
	
	/**
	 * Returns the path to the JAR or module where the given class is located.
	 *
	 * @param klass the class to inspect (must not be {@code null})
	 * @return URL string of the location, or message if not found
	 * @throws IllegalArgumentException if the input class is {@code null}
	 */
	public static String findJarFile(Class<?> klass) {
		if (klass == null) {
			throw new IllegalArgumentException("Class must not be null.");
		}
		
		String path = '/' + klass.getName().replace('.', '/') + ".class";
		URL jarLocation = klass.getResource(path);
		return jarLocation != null ? jarLocation.toString() : "Class not found in JAR or Module";
	}
	
	/**
	 * Provides detailed information about the class origin in structured format.
	 * If the class is located inside a Maven repository, it includes:
	 * <ul>
	 *     <li>{@code M2 Home}</li>
	 *     <li>{@code Folders/Package Structure}</li>
	 *     <li>{@code Class Location}</li>
	 * </ul>
	 *
	 * @param klass the class to inspect (must not be {@code null})
	 * @return Map with details or fallback info
	 * @throws IllegalArgumentException if the input class is {@code null}
	 */
	public static Map<String, String> findJarDetails(Class<?> klass) {
		if (klass == null) {
			throw new IllegalArgumentException("Class must not be null.");
		}
		
		String classPath = '/' + klass.getName().replace('.', '/') + ".class";
		URL jarLocation = klass.getResource(classPath);
		
		Map<String, String> info = new LinkedHashMap<>();
		if (jarLocation == null) {
			info.put("Class Location", "Class not found in JAR or Module");
			return info;
		}
		
		String url = jarLocation.toString();
		int m2Index = url.indexOf("/.m2/repository/");
		
		if (m2Index != -1) {
			String m2Home = url.substring(0, m2Index + 16);
			String restPath = url.substring(m2Index + 16);
			String folders = restPath.replaceAll("/[^/]+\\.jar!.*", "");
			String jarFileName = restPath.replaceAll(".*?([^/]+\\.jar!)", "$1");
			
			info.put("M2 Home", m2Home.replace("file:/", "").replace("/", "\\"));
			info.put("Folders/Package Structure", folders);
			info.put("Class Location", "jar:file:{" + m2Home + "}{" + folders + "}/" + jarFileName + classPath);
		} else {
			info.put("Class Location", url);
		}
		
		return info;
	}
}
