package com.github.yash777.myworld.security.common;

import java.io.Serializable;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class PayloadEncryptDecrypt {
	private String EncryptDecryptAESKey = "bVyYZ2ZsbFdENmh6VlNFQ3BmUHhXZz08";
	private String EncryptDecryptIVKey = "A1SPPygLKfztpjed";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Data @AllArgsConstructor @NoArgsConstructor @Builder @ToString
	static class PayLoadDTO implements Serializable {
		
/*
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.lang.String` from Object value (token `JsonToken.START_OBJECT`)
 at [Source: (String)"{"id":1,"name":"AAA"}"; line: 1, column: 1]
	at com.fasterxml.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:59)
	at com.fasterxml.jackson.databind.DeserializationContext.reportInputMismatch(DeserializationContext.java:1741)
 */
		/** use serialVersionUID from JDK 1.0.2 for interoperability */
	    @java.io.Serial
	    private static final long serialVersionUID = -6849794470754667710L;
	    
		private Long id;
		private String name;
	}
	public static void main(String[] args) {
		PayloadEncryptDecrypt obj = new PayloadEncryptDecrypt();
		
		PayLoadDTO payLoadDTO = PayLoadDTO.builder().id(1L).name("AAA").build();
		
		String encrypted  = obj.encryptPayload( payLoadDTO );
		System.out.println("encrypted :"+encrypted);
		
		PayLoadDTO decryptPayload = obj.decryptPayload(encrypted);
		System.out.println("decryptPayload :"+decryptPayload);
	}
	
	
	public String encryptPayload(Object payload) {
		try {
			logger.info("Encrypting  Payload......");
			String payloadAsString = new ObjectMapper().writeValueAsString(payload);
			Key aesKey = new SecretKeySpec(EncryptDecryptAESKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(EncryptDecryptIVKey.getBytes()));
			byte[] cipherText = cipher.doFinal(payloadAsString.getBytes());
			String encryptedPayload = Base64.getEncoder().encodeToString(cipherText);
			logger.info(" Payload encrypted successfully. {{}}" + encryptedPayload);
			return encryptedPayload;
		} catch (Exception ex) {
			logger.info("Exception occurred while encrypting  payload. Exception Msg: {{}}, Exception: {{}}",
					ex.getMessage(), ex);
			return null;
		}
	}

	public PayLoadDTO decryptPayload(String encryptedData) {
		try {
			logger.info("Decrypting  Payload...... {{}}", encryptedData);
			byte[] decoded = DatatypeConverter.parseBase64Binary(encryptedData);
			Key aesKey = new SecretKeySpec(EncryptDecryptAESKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(EncryptDecryptIVKey.getBytes()));
			String decryptedPayload = new String(cipher.doFinal(decoded));
			logger.info("decryptedPayload: {{}}", decryptedPayload);
			PayLoadDTO payload = new ObjectMapper().readValue(decryptedPayload, PayLoadDTO.class);
			logger.info(" Payload decrypted successfully. {{}}", new ObjectMapper().writeValueAsString(payload));
			return payload;
		} catch (Exception ex) {
			return null;
		}
	}
	
}
