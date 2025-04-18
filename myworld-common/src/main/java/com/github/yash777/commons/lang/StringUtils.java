package com.github.yash777.commons.lang;

import java.util.Objects;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Utility class for common String operations.
 */
public class StringUtils {
	/**
	 * Sample usage of StringUtil methods.
	 */
	public static void main(String[] args) {
		String rawText = getUnicodeSpaces()+" \tHello World! - " + getUnicodeSpaces();
		
		System.out.println("To demonstrate the trimming functionalities");
		System.out.println("Original text:               \"" + rawText + "\"");
		System.out.println("Standard trim():             \"" + rawText.trim() + "\"");
		System.out.println("Unicode-aware trimSpaces():  \"" + trimSpaces(rawText) + "\"");
		System.out.println("Unicode-aware trimLeading(): \"" + trimSpacesLeading(rawText) + "\"");
		System.out.println("Unicode-aware trimTrailing():\"" + trimSpacesTrailing(rawText) + "\"");
		System.out.println("Custom trimAdvanced():       \"" + trimAdvanced(rawText, getUnicodeSpaces()) + "\"");
		
		System.out.println("\nUtility Functions:");
		System.out.println("isEmpty(null):         " + isEmpty(null));
		System.out.println("isBlank(\"   \"):      " + isBlank("   "));
		System.out.println("capitalize(\"hello\"): " + capitalize("hello"));
		System.out.println("repeat(\"*\", 5):      " + repeat("*", 5));
		
		
		System.out.println("\nreplaceSubstringInBetween():");
		System.out.println(replaceSubstringInBetween("abcdef", "123", "b", "e")); // ab123ef
		System.out.println(replaceSubstringInBetween("apachelang", "-commons-", "apache", "lang")); // apache-commons-lang
		System.out.println(replaceSubstringInBetween("abcdef", "123", "x", "y")); // abcdef (no match)
	}
	
	private static final int INDEX_NOT_FOUND = -1;
	/**
	 * Replaces a substring within a given string that is enclosed between two substrings (`open` and `close`).
	 *
	 * <p>
	 * Preconditions:
	 * <ul>
	 *   <li>{@code str} must not be {@code null}.</li>
	 *   <li>{@code open}, {@code close}, and {@code replace} must not be {@code null}.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * If any of the preconditions are not met (i.e., {@code str} is {@code null} or any of
	 * {@code open}, {@code close}, {@code replace} are {@code null}), the method returns the
	 * original {@code str} without any modifications.
	 * </p>
	 *
	 * <p>
	 * Examples:
	 * <pre>
	 * replaceSubstringInBetween(null, "-", "a", null)      = null (Returns original string)
	 * replaceSubstringInBetween("a", "-", "a", "a")        = a (Returns original string as its length is less than 2)
	 * replaceSubstringInBetween("abc", "-", "a", "c")      = a-c
	 * replaceSubstringInBetween("abcdef", "-", "a", null)  = "abcdef" (If either `open` or `close` is null, returns original string)
	 * replaceSubstringInBetween("a", "abc", "", "")        = ""   (empty open/close substrings, returns original string)
	 * replaceSubstringInBetween("abcdef", "", "a", "f")    = "af"
	 * replaceSubstringInBetween("abcdef", null, "a", "f")  = "abcdef" (null replacement, returns original string)
	 * 
	 * * If both `open` and `close` are found within `str`, it constructs the modified string by combining the substring before `open`,
	 * the replacement `replace`, and the substring after `close`.
	 * replaceSubstringInBetween("apachelang", "-commons-", "apache", "lang") = "apache-commons-lang" 
	 * replaceSubstringInBetween("abcdef", "123", "a", "f") = "a123f"
	 * replaceSubstringInBetween("abcdef", "123", "b", "e") = "ab123ef"
	 * 
	 * * If either `open` or `close` is not found (`start` or `end` is -1), it returns the original str unchanged.
	 * replaceSubstringInBetween("abcdef", "123", "z", "a") = "abcdef" 
	 * replaceSubstringInBetween("abcdef", "123", "a", "g") = "abcdef"
	 * </pre>
	 * </p>
	 *
	 * @param str     the original string, must not be null
	 * @param replace the string to insert between open and close markers
	 * @param open    the starting marker string
	 * @param close   the ending marker string
	 * @return the modified string with the replacement inserted, or original if conditions not met
	 * 
	 * @see <a href="https://github.com/apache/commons-lang/pull/395">Apache Commons PR #395</a>
	 * @see <a href="https://github.com/Yash-777/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/StringUtils.java#L7111">Implementation Reference</a>
	 */
	public static String replaceSubstringInBetween(final String str, final String replace, final String open, final String close) {
		if (isEmpty(str) || isEmpty(open) || isEmpty(close) || replace == null || str.length() <= 2) {
			return str;
		}
		
		int start = str.indexOf(open);
		if (start != INDEX_NOT_FOUND) {
			int end = str.indexOf(close, start + open.length());
			if (end != INDEX_NOT_FOUND) {
				String preceding = str.substring(0, start + open.length());
				String succeeding = str.substring(end);
				return preceding + replace + succeeding;
			}
		}
		return str;
	}
	
	
	/**
	 * Checks if a string is null or empty.
	 *
	 * @param str the input string
	 * @return true if null or empty, false otherwise
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}
	
	/**
	 * Checks if a string is null, empty, or only contains whitespace (including Unicode spaces).
	 *
	 * @param str the input string
	 * @return true if blank, false otherwise
	 */
	public static boolean isBlank(String str) {
		return str == null || trimSpaces(str).isEmpty();
	}
	
	/**
	 * Capitalizes the first character of a string.
	 *
	 * @param str the input string
	 * @return string with the first character capitalized
	 */
	public static String capitalize(String str) {
		if (isEmpty(str)) return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Repeats a string n times.
	 *
	 * @param str     the input string
	 * @param count   number of repetitions
	 * @return the repeated string
	 */
	public static String repeat(String str, int count) {
		if (str == null || count <= 0) return "";
		return str.repeat(count);
	}
	
	/**
	 * Enum containing various Unicode whitespace and invisible characters with their code, name, and description
	 */
	@AllArgsConstructor @NoArgsConstructor @Getter
	enum UnicodeSpace {
		WHITESPACE("\u0020", ".", "SP - WhiteSpace"),
		ENSP ("\u2002", "ENSP", "EN SPACE"),
		EMSP ("\u2003", "EMSP", "EM SPACE"),
		EMSP3("\u2004", "3/EMSP", "THREE-PER-EM SPACE"),
		EMSP4("\u2005", "4/EMSP", "FOUR-PER-EM SPACE"),
		EMSP6("\u2006", "6/EMSP", "SIX-PER-EM SPACE"),
		FSP  ("\u2007", "FSP", "FIGURE SPACE"),
		PSP  ("\u2008", "PSP", "PUNCTUATION SPACE"),
		THSP ("\u2009", "THSP", "THIN SPACE"),
		HSP  ("\u200A", "HSP", "HAIR SPACE"),
		ZWSP ("\u200B", "ZWSP", "ZERO WIDTH SPACE"),
		ZWNJ ("\u200C", "ZWNJ", "ZERO WIDTH NON-JOINER"),
		ZWJ  ("\u200D", "ZWJ", "ZERO WIDTH JOINER"),
		LRM  ("\u200E", "LRM", "LEFT-TO-RIGHT MARK"),
		RLM  ("\u200F", "RLM", "RIGHT-TO-LEFT MARK"),
		LS  ("\u2028", "LS", "LINE SEPARATOR"),
		PS  ("\u2029", "PS", "PARAGRAPH SEPARATOR"),
		LRE ("\u202A", "LRE", "LEFT-TO-RIGHT EMBEDDING"),
		RLE ("\u202B", "RLE", "RIGHT-TO-LEFT EMBEDDING"),
		PDF ("\u202C", "PDF", "POP DIRECTIONAL FORMATTING"),
		LRO ("\u202D", "LRO", "LEFT-TO-RIGHT OVERRIDE"),
		RLO ("\u202E", "RLO", "RIGHT-TO-LEFT EMBEDDING"),
		NNBSP("\u202F", "NNBSP", "ZERO WIDTH SPACE"),
		MMSP("\u205F", "MMSP", "MEDIUM MATHEMATICAL SPACE"),
		WJ  ("\u2060", "WJ",   "WORD JOINER"),
		ZWNBSP("\uFEFF", "ZWNBSP", "ZERO WIDTH NO-BREAK SPACE"),
		FA  ("\u2061", "(FA)",   "Function application"),
		IT  ("\u2062", "(IT)",   "invisible times"),
		IS  ("\u2063", "(IS)",   "invisible separator"),
		IP  ("\u2064", "(IP)",   "invisible plus"),
		ISS  ("\u206A", "ISS",   "INHIBIT SYMMETRIC SWAPPING"),
		ASS  ("\u206B", "ASS",   "ACTIVATE SYMMETRIC SWAPPING"),
		IAFS ("\u206C", "IAFS",   "INHIBIT ARABIC FORM SHAPING"),
		AAFS ("\u206D", "AAFS",   "ACTIVATE ARABIC FORM SHAPING"),
		NADS ("\u206E", "NADS",   "NATIONAL DIGIT SHAPES"),
		NODS ("\u206F", "NODS",   "NOMINAL DIGIT SHAPES"),
		NBSP ("\u00A0", "NBSP", "NO-BREAK SPACE (&nbsp;)"),
		BACKSPACE1("\u0008", "BKSP", "BACKSPACE"),
		BACKSPACE("\u2408", "BS", "SYMBOL FOR BACKSPACE (␈)"),
		SPACE("\u2420", "SP", "SYMBOL FOR SPACE (␠)"),
		IDSP("\u3000", "IDSP", "IDEOGRAPHIC SPACE"),
		IHSPACE("\u303F", "ZWSP", "IDEOGRAPHIC HALF FILL SPACE"),
		TAB("	", "TAB", "TAB SPACE");
		
		private String code, name, description;
	}
	
	/**
	 * Returns a concatenated string of all defined Unicode space/invisible characters.
	 *
	 * @return all Unicode characters to be trimmed
	 */
	public static String getUnicodeSpaces() {
		StringBuilder codesStringBuilder = new StringBuilder();
		for (UnicodeSpace unicode : UnicodeSpace.values()) {
			codesStringBuilder.append(unicode.getCode());
		}
		return codesStringBuilder.toString();
	}
	
	/**
	 * Trims only the leading Unicode whitespace and invisible characters from a string.
	 *
	 * @param text the original string
	 * @return the string with leading spaces trimmed
	 */
	public static String trimSpacesLeading(String text) {
		String spaceTrimRegex = "^[" + Pattern.quote(getUnicodeSpaces()) + "]+";
		return text.replaceAll(spaceTrimRegex, "");
	}
	/**
	 * Trims only the trailing Unicode whitespace and invisible characters from a string.
	 *
	 * @param text the original string
	 * @return the string with trailing spaces trimmed
	 */
	public static String trimSpacesTrailing(String text) {
		String spaceTrimRegex = "[" + Pattern.quote(getUnicodeSpaces()) + "]+$";
		return text.replaceAll(spaceTrimRegex, "");
	}
	/**
	 * Trims leading and trailing Unicode whitespace and invisible characters from a string.
	 *
	 * @param text the original string
	 * @return the trimmed string
	 */
	public static String trimSpaces(String text) {
		String spaceTrimRegex = "^[" + Pattern.quote(getUnicodeSpaces()) + "]+|[" + Pattern.quote(getUnicodeSpaces()) + "]+$";
		return text.replaceAll(spaceTrimRegex, "");
	}
	
	/**
	 * Trims leading and trailing characters defined in the given skip string.
	 *
	 * @param value      the original string
	 * @param skipString a string of characters to be trimmed
	 * @return the trimmed string
	 */
	public static String trimAdvanced(String value, String skipString) {
		Objects.requireNonNull(value);
		
		int strLength = value.length();
		if (strLength == 0) return value;
		
		int len = value.length();
		int st = 0;
		char[] val = value.toCharArray();
		
		while ((st < len) && (skipString.indexOf(val[st]) >= 0)) {
			st++;
		}
		
		while ((st < len) && (skipString.indexOf(val[len - 1]) >= 0)) {
			len--;
		}
		
		return (st > len) ? "" : ((st > 0) || (len < strLength)) ? value.substring(st, len) : value;
	}
	
}
