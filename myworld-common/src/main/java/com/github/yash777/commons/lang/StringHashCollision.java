package com.github.yash777.commons.lang;

/**
 * Demonstrates the difference between ==, equals(), and hashCode() using Strings in Java.
 * Also shows an example of a hash code collision between two distinct strings: "Aa" and "BB".
 *
 * Key Concepts:
 * - `==` checks reference equality (same object in memory)
 * - `equals()` checks logical/content equality
 * - `hashCode()` provides a numeric representation used in hash-based collections like HashMap
 *
 * ⚠️ Even if two strings have the same hash code, they may not be equal.
 *
 * Usage:
 * - Run this class as a Java application to see the behavior printed in the console.
 * 
 * @author 🔐 Yash
 */
public class StringHashCollision {

	public static void main(String[] args) {
		String s1 = "Aa";
		String s2 = "BB";
		
		System.out.println("String s1 = \"Aa\";");
		System.out.println("String s2 = \"BB\";\n");
		
		System.out.println("s1 == s2: " + (s1 == s2));
		System.out.println("s1.equals(s2): " + s1.equals(s2));
		System.out.println("s1.hashCode(): " + s1.hashCode());
		System.out.println("s2.hashCode(): " + s2.hashCode());
		
		System.out.println("\nSummary:");
		if (s1.hashCode() == s2.hashCode()) {
			System.out.println("- Hash codes are equal (collision): " + s1.hashCode());
		}
		if (!s1.equals(s2)) {
			System.out.println("- But strings are not equal: \"" + s1 + "\" != \"" + s2 + "\"");
		}
	}
}