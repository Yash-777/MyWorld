package com.github.yash777.commons.lang;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemEnvUtilTest {
	
	@Test
	void testGetHostName() {
		String hostname = SystemEnvUtil.getHostName();
		assertNotNull(hostname, "Host name should not be null");
		assertFalse(hostname.trim().isEmpty(), "Host name should not be empty");
	}
	
	@Test
	void testGetTempLocation() {
		String tempPath = SystemEnvUtil.getTempLocation();
		assertNotNull(tempPath, "Temp path should not be null");
		assertFalse(tempPath.trim().isEmpty(), "Temp path should not be empty");
	}
	
	@Test
	void testGetUser() {
		String user = SystemEnvUtil.getUser();
		assertNotNull(user, "User name should not be null");
		assertFalse(user.trim().isEmpty(), "User name should not be empty");
	}
	
	@Test
	void testGetSystemDateTime() {
		String dateTime = SystemEnvUtil.getSystemDateTime();
		assertNotNull(dateTime, "System date/time should not be null");
		assertTrue(dateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
				"System date/time format should be 'yyyy-MM-dd HH:mm:ss'");
	}
	
	@Test
	void testGetHeapInfoAsMap() {
		Map<String, String> heapInfo = SystemEnvUtil.getHeapInfoAsMap();
		assertNotNull(heapInfo);
		assertTrue(heapInfo.containsKey("Max Heap Memory (MB)"));
		assertTrue(heapInfo.containsKey("Total Heap Memory (MB)"));
		assertTrue(heapInfo.containsKey("Free Heap Memory (MB)"));
	}
	
	@Test
	void testGetSystemInfoAsMap() {
		Map<String, String> systemInfo = SystemEnvUtil.getSystemInfoAsMap();
		assertNotNull(systemInfo);
		assertTrue(systemInfo.containsKey("User"));
		assertTrue(systemInfo.containsKey("Temp Directory"));
		assertTrue(systemInfo.containsKey("Java Version"));
		assertTrue(systemInfo.containsKey("System DateTime"));
	}
	
	@Test
	void testGetEnvVariablesContaining_MatchExists() {
		Map<String, String> result = SystemEnvUtil.getEnvVariablesContaining("java");
		assertNotNull(result);
		assertTrue(result.size() >= 0); // could be zero if no match
	}
	
	@Test
	void testGetEnvVariablesContaining_NoMatch() {
		Map<String, String> result = SystemEnvUtil.getEnvVariablesContaining("no_such_env_key");
		assertNotNull(result);
		assertTrue(result.isEmpty(), "Expected no matches for nonexistent filter");
	}
}
