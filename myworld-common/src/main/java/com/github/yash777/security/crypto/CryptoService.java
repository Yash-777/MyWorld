package com.github.yash777.security.crypto;

import java.security.AlgorithmParameters;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoService {
	private String key = "B&^0QUV^?^SQ.{D|]C[[(+hm'^e7|FJ}Ga-4$T54:(bgpyD,)K{fpE8~M,YMzvu";
	static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
	public static Date getDateFrom(String creationDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(creationDate);
		System.out.println("Date :"+date);
		return date;
	}
    public static String getDateString(Date enrollmentDate) {
    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String dateAsString = sdf.format(enrollmentDate);
        return dateAsString;
    }
	
	public static void main(String[] args) throws Exception {
		//String creationDate = "2023-12-29 10:09:34"; // yyyy-MM-dd HH:mm:ss
		String creationDate = "2023-12-29T10:09:34"; // yyyy-MM-dd'T'HH:mm:ss
		Date dateFrom = getDateFrom(creationDate);
		
		CryptoService obj = new CryptoService();
//		String UserName = "ymerugu@innominds.com", Password = "Yash@001";
//		String UserName = "rbuyya@innominds.com", Password = "D$Pr^0@dm!n%)";
//		String UserName = "sparupati@innominds.com", Password = "D$Pr^0@dm!n%)";
		String UserName = "svallepu@innominds.com", Password = "D$Pr^0@dm!n%)";
		for (int i=0; i<4; i++) {
			//String encode = obj.encode("", "ymerugu@innominds.com", dateFrom);
			String encode = obj.encode(Password, UserName, dateFrom);
			System.out.println("encoded:"+encode);
			
			String sql = "update User set password ='"+encode+"', CreatedDate = STR_TO_DATE('12/29/2023 10:09:34', '%m/%d/%Y %H:%i:%s') where emailId='"+UserName+"';";
			System.out.println(sql);
		}
		
	}
	
	public String encode(String rawPass, String salt, Date createdDate) throws Exception {
        byte[] saltBytes = salt.getBytes();
        String initVector = getDateString(createdDate);
        // Derive the key using PBKDF2
        SecretKeyFactory factory = SecretKeyFactory.getInstance(EncoderConstants.PBKDF2_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), saltBytes, EncoderConstants.ITERATIONS, EncoderConstants.KEY_LENGTH);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), EncoderConstants.AES_ALGORITHM);
        // Encrypt the raw password
        byte[] ivBytes = new byte[16];
   		System.arraycopy(initVector.getBytes(EncoderConstants.UTF_8), 0, ivBytes, 0, 16);
   		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
   		Cipher cipher = Cipher.getInstance(EncoderConstants.TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secret,ivParameterSpec);
        AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(rawPass.getBytes(EncoderConstants.UTF_8));
        // Prepend salt and createdDate bytes
        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);

        return new org.apache.commons.codec.binary.Base64().encodeToString(buffer);
    }

    
	static class EncoderConstants {

	    EncoderConstants() {
	    }

	    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	    public static final int ITERATIONS = 1024;
	    public static final int KEY_LENGTH = 128;
	    public static final String AES_ALGORITHM = "AES";
	    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	    public static final String UTF_8 = "UTF-8";
	    public static final int SALT_SIZE = 24;
	  
	}
}
