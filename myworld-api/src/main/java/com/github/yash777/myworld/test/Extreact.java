package com.github.yash777.myworld.test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Extreact {
	private static final SecureRandom random = new SecureRandom();
	
	public String extracted(int length, String customChars) {
		if (length < 1) {
            return "Password length must be greater than 0.";
        }

        StringBuilder characterPool = new StringBuilder();
        List<Character> passwordChars = new ArrayList<>();

        if (customChars != null && !customChars.isEmpty()) {
            characterPool.append(customChars);
        } else {
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
        
        this.method();
        this.method2();

        return finalPassword.toString();
	}
	
	private void method() {
		System.out.println("Dummy Call 1");
	}
	public void method2() {
		System.out.println("Dummy Call 2");
	}
	public void method3() {
		System.out.println("Dummy Call 3");
	}
}
