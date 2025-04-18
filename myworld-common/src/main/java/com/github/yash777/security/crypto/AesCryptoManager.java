package com.github.yash777.security.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for performing AES encryption and decryption with support for multiple cipher modes (ECB, CBC, GCM) and key sizes (128, 192, 256 bits).
 * <p>
 * Provides functionality to:
 * <ul>
 *     <li>Encrypt and decrypt text data</li>
 *     <li>Allows 128, 192, and 256-bit key sizes via {@link KeySize} enum</li>
 *     <li>Accepts IV as a string, derived from a date (dd-mm-yyyy), or generated randomly</li>
 *     <li>Auto-generates or loads existing keys from disk (named like <code>secret_128bit.key</code>)</li>
 *     <li>Log key information and encryption process via SLF4J</li>
 * </ul>
 * 
 * <b>Supported Cipher Modes:</b>
 * <ul>
 *     <li>ECB (Electronic Codebook)</li>
 *     <li>CBC (Cipher Block Chaining)</li>
 *     <li>GCM (Galois/Counter Mode)</li>
 * </ul>
 * 
 * 🔐 Author: Yash
 */
public final class AesCryptoManager {
	private static final Logger log = LoggerFactory.getLogger(AesCryptoManager.class);
	
	//https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#keygenerator-algorithms
	private static final String KeyGenerator_Algorithm = "AES";
	
	private static final int GCM_TAG_LENGTH = 128;
	private static final String KEY_FILE_PREFIX = "secret_";
	private static final String KEY_FILE_EXT = ".key";
	
	private AesCryptoManager() {}
	
	/**
	 * Enum representing supported AES key sizes(128, 192, and 256 bits).
	 * <p>Provides file naming convention for storing keys securely.
	 */
	public enum KeySize {
		AES_128(128),
		AES_192(192),
		AES_256(256);
		
		/**
		 * Number of bits in the AES key.
		 */
		public final int bits;
		
		KeySize(int bits) {
			this.bits = bits;
		}
		/**
		 * Returns the expected file name for this key size.
		 *
		 * @return  File name for storing the secret key (e.g., <code>secret_128bit.key</code>)
		 */
		public String getFileName() {
			return KEY_FILE_PREFIX + bits + "bit" + KEY_FILE_EXT;
		}
	}
	
	/**
	 * Enum representing AES cipher modes using standard transformation strings.
	 * <p>
	 * Each transformation follows the format: {@code Algorithm/Mode/Padding}.
	 * <ul>
	 *   <li>{@code AES/ECB/PKCS5Padding} - Electronic Codebook mode (no IV required)</li>
	 *   <li>{@code AES/CBC/PKCS5Padding} - Cipher Block Chaining mode (IV required)</li>
	 *   <li>{@code AES/GCM/NoPadding}    - Galois/Counter Mode (IV required, authenticated encryption)</li>
	 * </ul>
	 *
	 * <p>
	 * For more information, refer to the official Java documentation:<br>
	 * <a href="https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html#cipher-algorithm-names">
	 * Cipher Algorithm Standard Names</a>
	 */
	public enum CipherMode {
		ECB("AES/ECB/PKCS5Padding"),
		CBC("AES/CBC/PKCS5Padding"),
		GCM("AES/GCM/NoPadding");
		
		public final String transformation;
		
		CipherMode(String transformation) {
			this.transformation = transformation;
		}
	}
	
	/**
	 * Generates a new AES key or loads it from the file if already present.
	 *
	 * @param size the key size (128, 192, or 256-bit)
	 * @param dir the directory to store or load the key file
	 * @return the loaded or newly generated {@link SecretKey}
	 * @throws Exception if key generation or loading fails
	 */
	public static SecretKey getOrCreateKey(KeySize size, File dir) throws Exception {
		File keyFile = new File(dir, size.getFileName());
		if (keyFile.exists()) {
			log.info("Key file exists: {}", keyFile.getAbsolutePath());
			return loadKeyFromFile(keyFile);
		}
		
		log.info("Generating new AES {} key", size.bits);
		KeyGenerator keyGen = KeyGenerator.getInstance(KeyGenerator_Algorithm);
		keyGen.init(size.bits);
		SecretKey key = keyGen.generateKey();
		
		saveKeyToFile(keyFile, key);
		return key;
	}
	
	/**
	 * Saves a given secret key to a specified file.
	 *
	 * @param file the file to write the key into
	 * @param key the secret key to save
	 * @throws IOException if the file operation fails
	 */
	public static void saveKeyToFile(File file, SecretKey key) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(key.getEncoded());
		}
		log.info("Saved secret key to {}", file.getAbsolutePath());
	}
	
	/**
	 * Loads a secret key from the provided file.
	 *
	 * @param file the file containing the AES key bytes
	 * @return the restored {@link SecretKey}
	 * @throws IOException if reading the file fails
	 */
	public static SecretKey loadKeyFromFile(File file) throws IOException {
		byte[] encoded = new byte[(int) file.length()];
		try (FileInputStream in = new FileInputStream(file)) {
			in.read(encoded);
		}
		return new SecretKeySpec(encoded, KeyGenerator_Algorithm);
	}
	
	/**
	 * Encrypts text using specified cipher mode and IV (if required).
	 *
	 * @param data      Plaintext to encrypt
	 * @param key       AES secret key
	 * @param mode      Cipher mode enum
	 * @param ivSpec    IV specification (required for CBC/GCM)
	 * @return Encrypted Base64-encoded string
	 * @throws Exception If encryption fails
	 */
	public static String encrypt(String data, SecretKey key, CipherMode mode, IvParameterSpec ivSpec) throws Exception {
		Cipher cipher = getCipher(mode.transformation);
		if (mode == CipherMode.GCM) {
			GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivSpec.getIV());
			cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
		} else if (ivSpec != null) {
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		
		byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		byte[] finalData = ivSpec != null ? combine(ivSpec.getIV(), encrypted) : encrypted;
		
		return Base64.getEncoder().encodeToString(finalData);
	}
	
	/**
	 * Decrypts a Base64 encoded AES-encrypted string.
	 *
	 * @param encryptedBase64 Encrypted text in Base64
	 * @param key              AES secret key
	 * @param mode             Cipher mode enum
	 * @return Decrypted plaintext
	 * @throws Exception If decryption fails
	 */
	public static String decrypt(String encryptedBase64, SecretKey key, CipherMode mode) throws Exception {
		Cipher cipher = getCipher(mode.transformation);
		byte[] data = Base64.getDecoder().decode(encryptedBase64);
		
		IvParameterSpec ivSpec = null;
		byte[] actualData = data;
		
		if (mode != CipherMode.ECB) {
			byte[] iv = Arrays.copyOfRange(data, 0, 16);
			actualData = Arrays.copyOfRange(data, 16, data.length);
			ivSpec = new IvParameterSpec(iv);
		}
		
		if (mode == CipherMode.GCM) {
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, ivSpec.getIV()));
		} else if (ivSpec != null) {
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, key);
		}
		
		byte[] decrypted = cipher.doFinal(actualData);
		return new String(decrypted, StandardCharsets.UTF_8);
	}
	
	/**
	 * Returns a Cipher instance for a valid transformation.
	 *
	 * @param transformation AES transformation
	 * @return Cipher instance
	 * @throws Exception If transformation is invalid
	 */
	public static Cipher getCipher(String transformation) throws Exception {
		switch (transformation) {
		case "AES/ECB/PKCS5Padding":
		case "AES/CBC/PKCS5Padding":
		case "AES/GCM/NoPadding":
			return Cipher.getInstance(transformation);
		default:
			throw new IllegalArgumentException("Unsupported transformation: " + transformation);
		}
	}
	
	/**
	 * Creates an IV from a string. If shorter than 16 bytes, it's padded.
	 *
	 * @param str String to convert to IV
	 * @return IvParameterSpec
	 */
	public static IvParameterSpec ivFromString(String str) {
		byte[] iv = Arrays.copyOf(str.getBytes(StandardCharsets.UTF_8), 16);
		return new IvParameterSpec(iv);
	}
	
	/**
	 * Creates an IV based on a date's milliseconds (useful for predictable IVs).
	 *
	 * @param date Date to generate IV from
	 * @return IvParameterSpec
	 */
	public static IvParameterSpec ivFromDate(Date date) {
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(date.getTime());
		buffer.putLong(0L);
		return new IvParameterSpec(buffer.array());
	}
	
	/**
	 * Combines IV and encrypted data for output.
	 *
	 * @param iv   Initialization vector
	 * @param data Encrypted data
	 * @return Byte array with IV + data
	 */
	public static byte[] combine(byte[] iv, byte[] data) {
		byte[] combined = new byte[iv.length + data.length];
		System.arraycopy(iv, 0, combined, 0, iv.length);
		System.arraycopy(data, 0, combined, iv.length, data.length);
		return combined;
	}
	
	/**
	 * Logs the AES key in Base64 format (useful for inspection/debug).
	 *
	 * @param key AES secret key
	 */
	public static void printKeyInfo(SecretKey key) {
		log.info("Secret Key (Base64): {}", Base64.getEncoder().encodeToString(key.getEncoded()));
	}
	
	// SampleCryptoUsage - Optional main method for quick testing
	public static void main(String[] args) throws Exception {
		File keyDir = new File(System.getProperty("java.io.tmpdir"));
		AesCryptoManager.KeySize size = AesCryptoManager.KeySize.AES_192;
		String message = "Hello AES, I'm Yash.";
		
		SecretKey key = AesCryptoManager.getOrCreateKey(size, keyDir);
		AesCryptoManager.printKeyInfo(key);
		
		// Extra: Using IV from Date
		IvParameterSpec ivFromDate = AesCryptoManager.ivFromDate(new Date());
		//SecretKey key192 = AesCryptoManager.getOrCreateKey(size, keyDir);
		String encryptedDateIV = AesCryptoManager.encrypt(message, key, CipherMode.CBC, ivFromDate);
		String decryptedDateIV = AesCryptoManager.decrypt(encryptedDateIV, key, CipherMode.CBC);
		
		System.out.println("CBC (IV from Date) Encrypted: " + encryptedDateIV);
		System.out.println("CBC (IV from Date) Decrypted: " + decryptedDateIV);
		
		// GCM mode (secure)
		IvParameterSpec gcmIV = AesCryptoManager.ivFromString("SecureIV12345678");
		String encryptedGCM = AesCryptoManager.encrypt(message, key, CipherMode.GCM, gcmIV);
		String decryptedGCM = AesCryptoManager.decrypt(encryptedGCM, key, CipherMode.GCM);
		
		System.out.println("GCM Encrypted: " + encryptedGCM);
		System.out.println("GCM Decrypted: " + decryptedGCM);
		
		// CBC with random IV
		IvParameterSpec iv1 = new IvParameterSpec(new byte[16]); // Zero IV (Not secure)
		String encryptedCBC = AesCryptoManager.encrypt(message, key, CipherMode.CBC, iv1);
		String decryptedCBC = AesCryptoManager.decrypt(encryptedCBC, key, CipherMode.CBC);
		
		System.out.println("CBC Encrypted: " + encryptedCBC);
		System.out.println("CBC Decrypted: " + decryptedCBC);
		
		// Test all key sizes and modes
		for (AesCryptoManager.KeySize sizes : AesCryptoManager.KeySize.values()) {
			System.out.println("\n--- Testing AES Key Size: " + sizes.bits + " ---");
			SecretKey keys = AesCryptoManager.getOrCreateKey(sizes, keyDir);
			AesCryptoManager.printKeyInfo(keys);
			
			for (CipherMode mode : CipherMode.values()) {
				System.out.println("-> Testing CipherMode: " + mode.name());
				
				IvParameterSpec iv = null;
				if (!mode.equals(CipherMode.ECB)) {
					iv = AesCryptoManager.ivFromString("IVFor-" + mode.name());
				}
				
				String encrypted = AesCryptoManager.encrypt(message, keys, mode, iv);
				String decrypted = AesCryptoManager.decrypt(encrypted, keys, mode);
				
				System.out.println("Encrypted (" + mode + "): " + encrypted);
				System.out.println("Decrypted (" + mode + "): " + decrypted);
				System.out.println("--------------------------------------------------");
			}
		}
		
	}
}