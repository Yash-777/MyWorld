package com.github.yash777.commons.lang;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {
	
	@Test
	void testGetUnicodeSpaces_NotEmpty() {
		String unicodeSpaces = StringUtils.getUnicodeSpaces();
		assertNotNull(unicodeSpaces);
		assertFalse(unicodeSpaces.isEmpty());
	}
	
	@Test
	void testTrimSpaces() {
		String text = "\u200B\u200A Hello World! \u200B";
		String expected = "Hello World!";
		assertEquals(expected, StringUtils.trimSpaces(text));
	}
	
	@Test
	void testTrimSpacesLeading() {
		String text = "\u200B\u200A Hello World!";
		String expected = "Hello World!";
		assertEquals(expected, StringUtils.trimSpacesLeading(text));
	}
	
	@Test
	void testTrimSpacesTrailing() {
		String text = "Hello World! \u200B\u200A";
		String expected = "Hello World!";
		assertEquals(expected, StringUtils.trimSpacesTrailing(text));
	}
	
	@Test
	void testTrimAdvanced() {
		String text = "\u200B Hello World! \u200B";
		String expected = "Hello World!";
		assertEquals(expected, StringUtils.trimAdvanced(text, StringUtils.getUnicodeSpaces()));
	}
	
	/**
	 * Test method for 'StringUtils.replaceSubstringInBetween(String, String, String, String)'
	 */
	@Test
	public void testReplaceSubstringInBetween() {
		//JAVADOC TESTS START
		assertNull(StringUtils.replaceSubstringInBetween(null, "A", "O", "C"));
		assertEquals(StringUtils.replaceSubstringInBetween("a", "-", "a", "a"), "a");
		assertEquals(StringUtils.replaceSubstringInBetween("abc", "-", "a", "c"), "a-c");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "-", "a", null), "abcdef");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "-", null, "f"), "abcdef");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", null, "a", "f"), "abcdef");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "-", "a", ""),   "abcdef");
		assertEquals(StringUtils.replaceSubstringInBetween("", "abc", "", ""), "");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "", "a", "f"), "af");
		assertEquals(StringUtils.replaceSubstringInBetween("apachelang", "-commons-", "apache", "lang"), "apache-commons-lang");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "123", "a", "f"), "a123f");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "123", "b", "e"), "ab123ef");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "123", "", ""), "abcdef");
		assertEquals(StringUtils.replaceSubstringInBetween("abcdef", "123", "z", "a"), "abcdef");
		//JAVADOC TESTS END
	}
	
	@Test
	void testReplaceSubstringInBetween_Valid() {
		String original = "abcdef";
		String result = StringUtils.replaceSubstringInBetween(original, "123", "b", "e");
		assertEquals("ab123ef", result);
	}
	
	@Test
	void testReplaceSubstringInBetween_NotFound() {
		String original = "abcdef";
		String result = StringUtils.replaceSubstringInBetween(original, "123", "x", "z");
		assertEquals("abcdef", result);
	}
	
	@Test
	void testReplaceSubstringInBetween_NullInputs() {
		assertNull(StringUtils.replaceSubstringInBetween(null, "-", "a", "b"));
		assertEquals("abc", StringUtils.replaceSubstringInBetween("abc", null, "a", "b"));
		assertEquals("abc", StringUtils.replaceSubstringInBetween("abc", "-", null, "b"));
		assertEquals("abc", StringUtils.replaceSubstringInBetween("abc", "-", "a", null));
	}
	
	@Test
	void testReplaceSubstringInBetween_LengthEdgeCase() {
		assertEquals("a", StringUtils.replaceSubstringInBetween("a", "-", "a", "a"));
	}
}
