package com.github.yash777.security.crypto;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import lombok.Data;

/**
 * Encryption and Decryption of String data; PBE(Password Based Encryption and Decryption)
 * 
* @author 🔐 Yash
* 
* https://stackoverflow.com/a/32583766/5081877
* https://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
*/
//dependency:commons-codec
@SuppressWarnings("resource")
//@Slf4j
@Data 
public class CipherKeyEncryptDecrypt {
	public static void main(String[] args) throws Exception {
		System.out.println("Current JVM version - " + System.getProperty("java.version"));
		
		CipherKeyEncryptDecrypt obj = new CipherKeyEncryptDecrypt();
		
		secretPasswordKey = ENCRYPT_PASS_DB;
		
		String salt_UserName = "yash@gmail.com";
		Date dateFrom = getDateFrom("2023-12-29 10:09:34");
		String raw_Password = "Yash@001";
		String dbEncodeWithDate = "YWRtaW5AZHNwcm8uY29tMjAyMy0xMi0yOVQxMDowOUujxOjQ6K8UZpQ76Mgpd6Y=";
		
		String encoded = obj.encode(raw_Password, salt_UserName, null);
		System.out.println("encode : "+encoded);
		String encodeWithDate = obj.encode(raw_Password, salt_UserName, dateFrom);
		System.out.println("encodeWithDate : "+encodeWithDate);
		String DBDoecodedPassDate = obj.decode(dbEncodeWithDate, salt_UserName, dateFrom);
		System.out.println("DB DoecodedPassDate : "+DBDoecodedPassDate);
		
		obj.checkDefaultFlow();
	}
	private void checkDefaultFlow() throws Exception {
		String creationDate = "2023-12-29 10:09:34"; // yyyy-MM-dd HH:mm:ss
		//String creationDate = "2023-12-29T10:09:34"; // dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
		
		Date dateFrom = getDateFrom(creationDate);
		String salt_UserName = "yash@gmail.com";
		String raw_Password = "Yash@001";
		setSecretPasswordKey();
		for (int i = 0; i < 2; i++) {
			System.err.println("====================================================== Date:"+new Date());
			// Generated Unique String every time - as date is same
			String encodeWithDate = this.encode(raw_Password, salt_UserName, dateFrom);
			System.out.println("encodeWithDate : "+encodeWithDate);
			String doecodedPassDate = this.decode(encodeWithDate, salt_UserName, dateFrom);
			System.out.println("doecodedPassDate : "+doecodedPassDate);
			
			this.matchesDecoded(encodeWithDate, salt_UserName, null, raw_Password);
			this.matchesEncoded(encodeWithDate, salt_UserName, dateFrom, raw_Password);
			
			System.err.println("======================================================");
			
			// Generated non-Unique String every time - as date is same current-timestamp
			String encode = this.encode(raw_Password, salt_UserName, null);
			System.out.println("encode : "+encode);
			String doecodedPass = this.decode(encode, salt_UserName, null);
			System.out.println("doecodedPass : "+doecodedPass);
			
			this.matchesDecoded(encode, salt_UserName, raw_Password);
			this.matchesEncoded(encode, salt_UserName, raw_Password);
		}
	}
	
	// Define characters that will be used to generate the password
	
	static boolean isENCRYPT_PASS_From_File = false;
	// Change this to your desired key
	static String KEY_PATH = "D:/security/SecretPasswordKey.key";
	static String ENCRYPT_PASS_1 = "AW'X;hNxK7^VpogH#tXr(p1hT?!1L;)La.s6tc$T@NO%\"Fh,jN%dey5'PqBz6/9?";
	static String ENCRYPT_PASS_2 = "U,XXx:LO2Lk,^tqA%Ei=#1*Z,FVsEDV68M>>M$A1w3_ST?:|fnDCe&<#q1zvY'&i";
	static String ENCRYPT_PASS_DB = "B&^0QUV^?^SQ.{D|]C[[(+hm'^e7|FJ}Ga-4$T54:(bgpyD,)K{fpE8~M,YMzvu";
	
	static String UTF_8 = "UTF-8";
	static int SALT_SIZE = 24;
	
	static String secretPasswordKey;
	static void setSecretPasswordKey() {
		String generatedPassword = generateRandomPassword(PASSWORD_LENGTH);
		System.out.println("Generated secretKeyPassword: " + generatedPassword);
		ENCRYPT_PASS_1 = generatedPassword;
		
		if (isENCRYPT_PASS_From_File) {
			try {
				secretPasswordKey = new java.util.Scanner(new java.io.File(KEY_PATH)).useDelimiter("\\Z").next();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			secretPasswordKey = ENCRYPT_PASS_1;
		}
		
		System.err.println("Generated Salt Size(16) : " + generateRandomSalt(16));
		System.err.println("Generated Salt Size24(24) : " + generateRandomSalt(24));
		
		System.err.println("Generated Salt Based on User Input (16): " + generateUserSpecificSalt("Yash", 16));
	}
	
	static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[{]}|;:'\",<.>/?";
	static final int PASSWORD_LENGTH = 64; // Length of the generated password
	// Method to generate a random password
	private static String generateRandomPassword(int length) {
		SecureRandom random = new SecureRandom(); // Secure random number generator
		StringBuilder password = new StringBuilder(length);
		
		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length()); // Random index to pick a character
			password.append(CHARACTERS.charAt(randomIndex));
		}
		
		return password.toString(); // Return the generated password as a string
	}
	// Generate a random salt of the required length
	private static String generateRandomSalt(int saltSize) {
		java.security.SecureRandom random = new java.security.SecureRandom();
		byte[] salt = new byte[saltSize]; // Define the required salt size (e.g., 16 or 24 bytes)
		//Salt Size: The length of the salt (in bytes) will be specified by the saltSize parameter.
		// A 16-byte salt will produce a Base64 string of 24 characters.
		// A 24-byte salt will produce a Base64 string of 32 characters.
		random.nextBytes(salt); // Fill the byte array with random values
		//return salt; - byte[]
		String generatedSalt = java.util.Base64.getEncoder().encodeToString(salt);
		System.out.println("Generated Salt as Base64-encoded string: " + generatedSalt);
		return generatedSalt; // Return as Base64 string
	}
	//Generating Salt Based on User Input (e.g., username):
	private static String generateUserSpecificSalt(String userName, int saltSize) {
		//System.currentTimeMillis(): This is used to make the salt unique for each instance, based on the current time (you can use any string that ensures uniqueness).
		String saltBase = userName + System.currentTimeMillis(); // Combine username with a timestamp
		System.out.println("saltBase:"+saltBase);
		
		//Random Bytes: SecureRandom will still generate the cryptographically secure random salt.
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[saltSize]; // Generate random bytes of the required salt size
		random.nextBytes(salt);
		
		// Combine the base salt with the username-based salt and return the final Base64 encoded salt
		return java.util.Base64.getEncoder().encodeToString(salt);
	}
	
	
	static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	// Convert Date to String
	public static String getStringFrom(Date enrollmentDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String dateAsString = sdf.format(enrollmentDate);
		return dateAsString;
	}
	// Convert String to Date
	public static Date getDateFrom(String creationDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(creationDate);
		System.out.println("Date :"+date);
		return date;
	}
	
	byte[] saltBytes = new byte[SALT_SIZE];
	
	static int KEY_LENGTH = 128; // 256 // Define key length (256 bits)
	static int ITERATIONS = 1024; // Define number of iterations - 10000
	static String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"; // PBKDF2WithHmacSHA256 - // PBKDF2 with HMAC-SHA256
	// Helper method to generate SecretKey
	private SecretKey generateSecretKey(String salt) throws Exception {
		this.saltBytes = salt.getBytes();
		SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		PBEKeySpec spec = new PBEKeySpec(secretPasswordKey.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
		return factory.generateSecret(spec);
	}
	// Helper method to generate IvParameterSpec based on createdDate
	private IvParameterSpec generateIvParameterSpec(Date createdDate) throws UnsupportedEncodingException {
		String ivStrDate = getStringFrom(createdDate);
		System.out.println("IvParameterSpec based on Date :"+ivStrDate);
		
		byte[] ivBytes = new byte[16]; // AES requires 16-byte IV
		byte[] ivSource = ivStrDate.getBytes(UTF_8);
		System.arraycopy(ivSource, 0, ivBytes, 0, Math.min(ivSource.length, ivBytes.length));
		//System.arraycopy(ivStrDate.getBytes(UTF_8), 0, ivBytes, 0, 16);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
		
		return ivParameterSpec;
	}
	// Extract IV bytes from AlgorithmParameters
	private byte[] getIvBytesFromParams(AlgorithmParameters params) {
		try {
			return params.getParameterSpec(IvParameterSpec.class).getIV();
		} catch (Exception e) {
			return new byte[16]; // Default empty iv if there's an error
		}
	}
	// Helper method to encode result (salt + iv + encrypted data)
	private String encodeResult(byte[] saltBytes, AlgorithmParameters params, byte[] encryptedTextBytes) {
		byte[] ivBytes = getIvBytesFromParams(params);
		// prepend salt and iv
		byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
		System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
		System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
		System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);
		return java.util.Base64.getEncoder().encodeToString(buffer);
	}
	
	static String AES_ALGORITHM = "AES"; // SecretKeySpec - ALOG (AES)
	static String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // Cipher = "AES/CBC/PKCS5Padding"
	public String encode(String rawPass, String salt, Date createdDate) throws Exception {
		SecretKey secretKey = generateSecretKey(salt);
		
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
		
		// Encrypt the raw password
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		if (createdDate != null) { // AlgorithmParameterSpec
			IvParameterSpec ivParameterSpec = generateIvParameterSpec(createdDate);
			
			cipher.init(Cipher.ENCRYPT_MODE, secret, ivParameterSpec);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, secret);
		}
		
		AlgorithmParameters params = cipher.getParameters();
		byte[] encryptedTextBytes = cipher.doFinal(rawPass.getBytes(UTF_8));
		
		return encodeResult(saltBytes, params, encryptedTextBytes);
		//return new Base64().encodeToString(buffer);
	}
	
	public String decode(String encryptedText, String salt, Date createdDate) throws Exception {
		SecretKey secretKey = generateSecretKey(salt);
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		// strip off the salt and iv
		ByteBuffer buffer = ByteBuffer.wrap(new Base64().decode(encryptedText));
		buffer.get(saltBytes, 0, saltBytes.length);
		byte[] ivBytes1 = new byte[cipher.getBlockSize()];
		buffer.get(ivBytes1, 0, ivBytes1.length);
		byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];
		buffer.get(encryptedTextBytes);
		// Deriving the key
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
		byte[] decryptedTextBytes = null;
		try {
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			System.err.println("Error while decrypt ::"+ e.getMessage());
		}
		return new String(decryptedTextBytes);
	}
	
	public boolean matchesDecoded(String encPass, String salt, String rawPass) throws Exception {
		String decodedPass = decode(encPass, salt, null);
		System.out.println("Fetch DB Password decode and match with raw password - IsMatched:"+decodedPass.equals(rawPass));
		return decodedPass.equals(rawPass);
	}
	public boolean matchesEncoded(String encPass, String salt, String rawPass) throws Exception {
		String reEncodedPassword = encode(rawPass, salt, null);
		System.out.println("Fetch DB Password and match the raw password by decoding it - IsMatched:"+ reEncodedPassword.equals(encPass));
		return reEncodedPassword.equals(encPass); // 
	}
	
	public boolean matchesDecoded(String encPass, String salt, Date createdDate, String rawPass) throws Exception {
		String decodedPass = decode(encPass, salt, null);
		System.out.println("Fetch DB Password decode and match with raw password - IsMatched:"+decodedPass.equals(rawPass));
		return decodedPass.equals(rawPass); // 
	}
	public boolean matchesEncoded(String encPass, String salt, Date createdDate, String rawPass) throws Exception {
		String reEncodedPassword = encode(rawPass, salt, createdDate);
		System.out.println("Fetch DB Password and match the raw password by decoding it - IsMatched:"+reEncodedPassword.equals(encPass));
		return reEncodedPassword.equals(encPass); // 
	}
	
	
	// Common Validations
	static boolean isValidPasswordPattern (String password) {
		String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%&*()-+=^])(?=\\S+$).{8,30}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		
		return matcher.matches();
	}
}