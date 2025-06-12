
package com.github.yash777.commons.file;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FindClassBelongsToJarTest {
	
	@Test
	@DisplayName("Should return a valid URL for JDK class")
	void testFindJarFileForJDKClass() {
		String result = FindClassFileBelongsToJar.findJarFile(String.class);
		assertNotNull(result);
		assertTrue(result.contains("java/lang/String.class") || result.contains("jrt:/"));
	}
	
	@Test
	@DisplayName("Should throw exception for null input")
	void testFindJarFileWithNull() {
		assertThrows(IllegalArgumentException.class, () -> FindClassFileBelongsToJar.findJarFile(null));
	}
	
	@Test
	@DisplayName("Should extract Maven details for third-party class")
	void testFindJarDetailsForApacheClass() {
		Map<String, String> details = FindClassFileBelongsToJar.findJarDetails(org.apache.commons.lang3.StringUtils.class);
		assertNotNull(details);
		assertTrue(details.get("Class Location") != null);
	}
	
	@Test
	@DisplayName("Should fallback for non-Maven class (JDK)")
	void testFindJarDetailsForJDKClass() {
		Map<String, String> details = FindClassFileBelongsToJar.findJarDetails(String.class);
		assertNotNull(details);
		assertTrue(details.get("Class Location").contains("java/lang/String.class") || details.get("Class Location").contains("jrt:/"));
	}
	
	@Test
	@DisplayName("Should throw exception for null in detailed method")
	void testFindJarDetailsWithNull() {
		assertThrows(IllegalArgumentException.class, () -> FindClassFileBelongsToJar.findJarDetails(null));
	}
}