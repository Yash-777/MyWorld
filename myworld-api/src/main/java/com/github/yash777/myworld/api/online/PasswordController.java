package com.github.yash777.myworld.api.online;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.yash777.security.crypto.CipherKeyEncryptDecrypt;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller providing utilities for password generation, encoding, and decoding.
 *
 * <p>This controller supports the following endpoints:</p>
 * <ul>
 *   <li><b>GET /myapp/pswd/generate/default</b> — Generates a random password with configurable rules.</li>
 *   <li><b>GET /myapp/pswd/encode</b> — Encrypts a password with optional salt, timestamp, and custom key set.</li>
 *   <li><b>GET /myapp/pswd/decode</b> — Decrypts an encoded password using the same configuration used for encoding.</li>
 * </ul>
 *
 * <p>Intended for secure password management and reversible encryption testing. For production,
 * always ensure keys are stored securely and HTTPS is used.</p>
 *
 * @author
 *     Yash
 * @since
 *     1.0
 */
@Tag(name = "Password Module", description = "Password Generation, Encoding, and Decoding APIs")
@RestController
@RequestMapping("/myapp/pswd")
public class PasswordController {
	
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL_CHARS = "!@#$%^&*()-_+=";
	
	private static final SecureRandom random = new SecureRandom();
	
	/**
	 * Generates a random password based on the provided parameters.
	 *
	 * <p>The password will contain at least one character from each selected character group
	 * (uppercase, lowercase, digits, special characters). The final password is randomized
	 * and shuffled to ensure unpredictability.</p>
	 *
	 * @param length the desired password length (minimum 4)
	 * @param useUppercase whether to include uppercase letters (A–Z)
	 * @param useLowercase whether to include lowercase letters (a–z)
	 * @param useDigits whether to include numeric digits (0–9)
	 * @param useSpecial whether to include special characters (e.g., !@#$%)
	 * @return the generated password as a string, or an error message if invalid input
	 */
	@GetMapping("/generate/default")
	public String generatePassword(
			@RequestParam int length,
			@Parameter(description = "UPPERCASE = \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\"")
			@RequestParam(required = false, defaultValue = "true") boolean useUppercase,
			@Parameter(description = "LOWERCASE = \"abcdefghijklmnopqrstuvwxyz\"")
			@RequestParam(required = false, defaultValue = "true") boolean useLowercase,
			@Parameter(description = "DIGITS = \"0123456789\"")
			@RequestParam(required = false, defaultValue = "true") boolean useDigits,
			@Parameter(description = "SPECIAL_CHARS = \"!@#$%^&*()-_+=\"")
			@RequestParam(required = false, defaultValue = "true") boolean useSpecial
			) {
		if (length < 4) {
			return "Password length must be at least 4";
		}
		
		StringBuilder characterPool = new StringBuilder();
		List<Character> passwordChars = new ArrayList<>();
		
		if (useUppercase) {
			characterPool.append(UPPERCASE);
			passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
		}
		
		if (useLowercase) {
			characterPool.append(LOWERCASE);
			passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
		}
		
		if (useDigits) {
			characterPool.append(DIGITS);
			passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
		}
		
		if (useSpecial) {
			characterPool.append(SPECIAL_CHARS);
			passwordChars.add(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
		}
		
		if (characterPool.length() == 0) {
			return "At least one character type must be selected.";
		}
		
		while (passwordChars.size() < length) {
			char ch = characterPool.charAt(random.nextInt(characterPool.length()));
			passwordChars.add(ch);
		}
		
		Collections.shuffle(passwordChars);
		
		StringBuilder finalPassword = new StringBuilder();
		for (char c : passwordChars) {
			finalPassword.append(c);
		}
		
		return finalPassword.toString();
	}
	
	/**
	 * Encodes (encrypts) a given password using the provided salt, optional creation date, and character set.
	 *
	 * <p>This endpoint supports reversible encryption using {@link CipherKeyEncryptDecrypt}.
	 * The encryption behavior depends on the provided salt and optional creation date.</p>
	 *
	 * @param salt_UserName a unique user identifier or salt (e.g., email)
	 * @param raw_Password the raw plaintext password to be encrypted
	 * @param creationDate optional timestamp in format {@code yyyy-MM-dd HH:mm:ss} (used for date-based keys)
	 * @param customChars optional custom key character set to override the default secret key
	 * @return the encoded password string
	 * @throws Exception if encryption fails or parameters are invalid
	 */
	@GetMapping("/encode")
	public String encodePassword(
			@Parameter(description = "Salted user identifier (e.g., email)", example = "yash@myworld.com")
			@RequestParam String salt_UserName,
			@Parameter(description = "Raw password string", example = "Y@sh^0@dm!n%)")
			@RequestParam String raw_Password,
			@Parameter(description = "Creation date in format yyyy-MM-dd HH:mm:ss (optional)", example = "2025-09-27 10:07:37")
			@RequestParam(required = false) String creationDate,
			@Parameter(description = "Custom character set for encryption (optional)",
			example = "B&^0QUV^?^SQ.{D|]C[[(+hm'^e7|FJ}Ga-4$T54:(bgpyD,)K{fpE8~M,YMzvu")
			@RequestParam(required = false) String customChars
			) throws Exception {
		
		CipherKeyEncryptDecrypt cipher = new CipherKeyEncryptDecrypt();
		
		// Parse creation date if provided
		LocalDateTime parsedDate = null;
		if (creationDate != null && !creationDate.isBlank()) {
			try {
				parsedDate = LocalDateTime.parse(creationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid creationDate format. Use 'yyyy-MM-dd HH:mm:ss'");
			}
		}
		
		// Select appropriate secret key
		if (customChars != null && !customChars.isBlank()) {
			CipherKeyEncryptDecrypt.secretPasswordKey = customChars;
		} else {
			CipherKeyEncryptDecrypt.secretPasswordKey = CipherKeyEncryptDecrypt.ENCRYPT_PASS_DB;
		}
		
		if (parsedDate != null) {
			Date dateFrom = CipherKeyEncryptDecrypt.getDateFrom(creationDate);
			String encoded = cipher.encode(raw_Password, salt_UserName, dateFrom);
			return encoded;
		} else {
			String encoded = cipher.encode(raw_Password, salt_UserName, null);
			return encoded;
		}
	}
	
	/**
	 * Decodes (decrypts) an encoded password back to its original plaintext value.
	 *
	 * <p>The decoding process must match the same salt, creation date, and key configuration
	 * used during encoding to ensure correct decryption.</p>
	 *
	 * @param salt_UserName the same salt (e.g., email) used for encoding
	 * @param encoded_Password the encoded password string to decrypt
	 * @param creationDate optional timestamp used during encryption (format {@code yyyy-MM-dd HH:mm:ss})
	 * @param customChars optional custom character set if one was used during encryption
	 * @return the decrypted (original) password
	 * @throws Exception if decryption fails or inputs are invalid
	 */
	@GetMapping("/decode")
	public String decodePassword(
			@Parameter(description = "Salted user identifier (e.g., email)", example = "yash@myworld.com")
			@RequestParam String salt_UserName,
			@Parameter(description = "Encoded password string",
			example = "eWFzaEBteXdvcmxkLmNvbTIwMjUtMDktMjcgMTA6MDdA11S9NKT1QWP0n93JC682")
			@RequestParam String encoded_Password,
			@Parameter(description = "Creation date in format yyyy-MM-dd HH:mm:ss (optional)",
			example = "2025-09-27 10:07:37")
			@RequestParam(required = false) String creationDate,
			@Parameter(description = "Custom character set for encryption (optional)",
			example = "B&^0QUV^?^SQ.{D|]C[[(+hm'^e7|FJ}Ga-4$T54:(bgpyD,)K{fpE8~M,YMzvu")
			@RequestParam(required = false) String customChars
			) throws Exception {
		
		CipherKeyEncryptDecrypt cipher = new CipherKeyEncryptDecrypt();
		
		// Parse creation date if provided
		LocalDateTime parsedDate = null;
		if (creationDate != null && !creationDate.isBlank()) {
			try {
				parsedDate = LocalDateTime.parse(creationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid creationDate format. Use 'yyyy-MM-dd HH:mm:ss'");
			}
		}
		
		// Select appropriate secret key
		if (customChars != null && !customChars.isBlank()) {
			CipherKeyEncryptDecrypt.secretPasswordKey = customChars;
		} else {
			CipherKeyEncryptDecrypt.secretPasswordKey = CipherKeyEncryptDecrypt.ENCRYPT_PASS_DB;
		}
		
		if (parsedDate != null) {
			Date dateFrom = CipherKeyEncryptDecrypt.getDateFrom(creationDate);
			return cipher.decode(encoded_Password, salt_UserName, dateFrom);
		} else {
			return cipher.decode(encoded_Password, salt_UserName, null);
		}
	}
}
