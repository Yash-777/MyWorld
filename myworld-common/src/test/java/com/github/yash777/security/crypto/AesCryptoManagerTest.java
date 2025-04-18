package com.github.yash777.security.crypto;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AesCryptoManagerTest {
	
	static final String MESSAGE = "Hello AES Unit Test!";
	static File keyDir;
	
	@BeforeAll
	static void setup() {
		keyDir = new File(System.getProperty("java.io.tmpdir"));
	}
	
	@Test
	void testAllKeySizesAndModes() throws Exception {
		for (AesCryptoManager.KeySize size : AesCryptoManager.KeySize.values()) {
			SecretKey key = AesCryptoManager.getOrCreateKey(size, keyDir);
			assertNotNull(key);
			AesCryptoManager.printKeyInfo(key);
			
			for (AesCryptoManager.CipherMode mode : AesCryptoManager.CipherMode.values()) {
				IvParameterSpec iv = mode == AesCryptoManager.CipherMode.ECB ? null : AesCryptoManager.ivFromString("TestVector123456");
				String encrypted = AesCryptoManager.encrypt(MESSAGE, key, mode, iv);
				String decrypted = AesCryptoManager.decrypt(encrypted, key, mode);
				
				assertNotNull(encrypted);
				assertEquals(MESSAGE, decrypted, "Decryption failed for mode: " + mode.name());
			}
		}
	}
	
	@Test
	void testIVFromString() {
		IvParameterSpec iv = AesCryptoManager.ivFromString("TestIV");
		assertNotNull(iv);
		assertEquals(16, iv.getIV().length);
	}
	
	@Test
	void testIVFromDate() {
		IvParameterSpec iv = AesCryptoManager.ivFromDate(new Date());
		assertNotNull(iv);
		assertEquals(16, iv.getIV().length);
	}
	
	@Test
	void testKeyFilePersistence() throws Exception {
		AesCryptoManager.KeySize size = AesCryptoManager.KeySize.AES_128;
		File keyFile = new File(keyDir, size.getFileName());
		
		SecretKey generatedKey = AesCryptoManager.getOrCreateKey(size, keyDir);
		assertTrue(keyFile.exists());
		
		SecretKey loadedKey = AesCryptoManager.loadKeyFromFile(keyFile);
		assertArrayEquals(generatedKey.getEncoded(), loadedKey.getEncoded());
	}
	
	@Test
	void testCombineMethod() {
		byte[] iv = "1234567890123456".getBytes();
		byte[] data = "cipherData".getBytes();
		
		byte[] combined = AesCryptoManager.combine(iv, data);
		assertEquals(26, combined.length); // 16 + 10
	}
	
	@Test
	void testPrintKeyInfo() throws Exception {
		SecretKey key = AesCryptoManager.getOrCreateKey(AesCryptoManager.KeySize.AES_256, keyDir);
		assertDoesNotThrow(() -> AesCryptoManager.printKeyInfo(key));
	}
}