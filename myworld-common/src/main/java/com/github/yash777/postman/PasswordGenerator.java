package com.github.yash777.postman;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class PasswordGenerator { // Java Decompiler Online: https://jdec.app/
   private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
   private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
   private static final String DIGITS = "0123456789";
   private static final String SPECIAL_CHARS = "!@#$%^&*()-_+=";
   private static final SecureRandom random = new SecureRandom();

   private static String createPassword(int charLength) {
      StringBuilder var1 = new StringBuilder();
      var1.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
      var1.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
      var1.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
      var1.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
      String characterPool = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;

      for(int var3 = 4; var3 < charLength; ++var3) {
         var1.append(characterPool.charAt(random.nextInt(characterPool.length())));
      }

      ArrayList<Character> var8 = new ArrayList<Character>();
      char[] var4 = var1.toString().toCharArray();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var4[var6];
         var8.add(var7);
      }

      Collections.shuffle(var8);
      StringBuilder var9 = new StringBuilder();
      Iterator<Character> var10 = var8.iterator();

      while(var10.hasNext()) {
         char var11 = (Character)var10.next();
         var9.append(var11);
      }

      return var9.toString();
   }

   public static void main(String[] var0) {
      System.out.println("Generated Password (12): " + generatePassword(12, false, false, false, false, "jAid5(9D!"));//jDdi5(ii)5((, Djd(_!!DjDdi
      
      System.out.println("Generated Password (12): " +	 createPassword(12));
      System.out.println("Generated Password (16): " + createPassword(16));
      System.out.println("Generated Password (20): " + createPassword(20));
      System.out.println("Generated Password (30): " + createPassword(30));
      System.out.println("Generated Password (32): " + createPassword(32));
   }
   
  public static  java.lang.String generatePassword(
	         int length,
	        boolean useUppercase,
	        boolean useLowercase,
	        boolean useDigits,
	        boolean useSpecial,
	        String customChars
	) {
	    if (length < 1) {
	        return "Password length must be greater than 0.";
	    }

	    StringBuilder characterPool = new StringBuilder();
	    List<Character> passwordChars = new ArrayList<>();

	    if (customChars != null && !customChars.isEmpty()) {
	        characterPool.append(customChars);
	    } else {
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
	    }

	    if (characterPool.length() == 0) {
	        return "No characters available to generate password.";
	    }

	    while (passwordChars.size() < length) {
	        passwordChars.add(characterPool.charAt(random.nextInt(characterPool.length())));
	    }

	    Collections.shuffle(passwordChars);

	    StringBuilder finalPassword = new StringBuilder();
	    for (char c : passwordChars) {
	        finalPassword.append(c);
	    }

	    return finalPassword.toString();
	}
}