package com.github.yash777.myworld.api.online;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>Text Analysis REST API</b></p>
 *
 * <p>This endpoint allows you to analyze input text for the following:</p>
 * <ul>
 *   <li>Total characters (with and without spaces)</li>
 *   <li>Number of words</li>
 *   <li>Number of sentences</li>
 *   <li>Number of spaces</li>
 *   <li>Number of paragraphs</li>
 * </ul>
 *
 * <p><b>Online tools for reference:</b></p>
 * <ul>
 *   <li><a href="https://www.charactercountonline.com/">Character Count Online</a></li>
 *   <li><a href="https://www.grammarly.com/character-counter">Grammarly Character Counter</a></li>
 *   <li><a href="https://quillbot.com/character-counter">QuillBot Character Counter</a></li>
 * </ul>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * GET /text/character-counter?input=Hello+world!+Welcome+to+Java.
 * </pre>
 * 
 * @author 🔐 Yashwanth
 */
@RestController
@RequestMapping("/text")
public class TextAnalysisController {
	
	/**
	 * Analyze the given input text and return text metrics.
	 *
	 * @param input The text to be analyzed.
	 * @return A map containing character, word, space, sentence, and paragraph counts.
	 */
	@GetMapping("/character-counter")
	public Map<String, Object> analyzeText(
			@Parameter(
					description = "Text input to be analyzed",
					example = "Hello world! Welcome to Java."
					)
			@RequestParam(required = true)
			String input
			) {
		Map<String, Object> result = new HashMap<>();
		
		if (input == null || input.isBlank()) {
			result.put("error", "Input text cannot be empty.");
			return result;
		}
		
		String trimmedInput = input.trim();
		
		int totalChars = trimmedInput.length(); // Including spaces
		int charCount = trimmedInput.replaceAll("\\s", "").length(); // Excluding all whitespace
		int spaceCount = totalChars - charCount;
		int wordCount = trimmedInput.split("\\s+").length;
		
		// Sentences counted using punctuation delimiters (., !, ?) followed by space or end of line
		int sentenceCount = trimmedInput.split("[.!?](\\s|$)").length;
		if (trimmedInput.isEmpty()) sentenceCount = 0;
		
		// Paragraphs separated by two or more newlines
		int paragraphCount = trimmedInput.split("(?m)(\\r?\\n){2,}").length;
		
		result.put("Words", wordCount);
		result.put("Characters without spaces", charCount);
		result.put("Spaces", spaceCount);
		result.put("Characters with spaces", totalChars);
		result.put("Sentences", sentenceCount);
		result.put("Paragraphs", paragraphCount);
		
		return result;
	}
	
	/**
	 * Standalone method to test text analysis logic without Spring Boot.
	 */
	public static void main(String[] args) {
		TextAnalysisController analyzer = new TextAnalysisController();
		
		System.out.println("🔹 Simple Sentence:");
		System.out.println(analyzer.analyzeText("Hello world! Welcome to Java."));
		
		System.out.println("\n🔹 Short Paragraph:");
		System.out.println(analyzer.analyzeText(
				"The quick brown fox jumps over the lazy dog. This sentence contains all the letters of the English alphabet."
				));
		
		String multiParagraph = """
			It was a bright cold day in April, and the clocks were striking thirteen. Winston Smith, his chin nuzzled into his breast in an effort to escape the vile wind, slipped quickly through the glass doors of Victory Mansions.
			
			He was not quickly enough to prevent a swirl of gritty dust from entering along with him.
			""";
		
		System.out.println("\n🔹 Multiple Paragraphs:");
		System.out.println(analyzer.analyzeText(multiParagraph));
		
		System.out.println("\n🔹 Technical Description:");
		System.out.println(analyzer.analyzeText(
				"In Java, the ObjectMapper class from the Jackson library is used to serialize Java objects into JSON and deserialize JSON into Java objects. It provides methods like writeValueAsString() and readValue() for conversion."
				));
		
		System.out.println("\n🔹 Lorem Ipsum Paragraph:");
		System.out.println(analyzer.analyzeText(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
				));
	}
}