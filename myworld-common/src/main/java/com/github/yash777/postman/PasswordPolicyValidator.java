package com.github.yash777.postman;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * PasswordPolicyValidator validates passwords for users based on their role.
 * 
 * Test custom banned password list: https://learn.microsoft.com/en-us/entra/identity/authentication/tutorial-configure-custom-password-protection
 * 
 * <p><b>Character Class Requirements (common to all users):</b></p>
 * <ul>
 *   <li>✅ At least one uppercase letter (A–Z)</li>
 *   <li>✅ At least one lowercase letter (a–z)</li>
 *   <li>✅ At least one digit (0–9)</li>
 *   <li>✅ At least one special character (e.g., !@#$%^&*())</li>
 * </ul>
 *
 * <p><b>🔐 Requirement 3.3.10.1 - Regular Users:</b></p>
 * <ul>
 *   <li>Minimum password length: 12 characters</li>
 *   <li>Must include all 4 character classes</li>
 * </ul>
 *
 * <p><b>🔐 Requirement 3.3.10.2 - Privileged Users (Admins):</b></p>
 * <ul>
 *   <li>Minimum password length: 16 characters</li>
 *   <li>Must include all 4 character classes</li>
 * </ul>
 * 
 * BRUTE-FORCE PROTECTION: To avoid 
Brute-force protection helps prevent attackers from guessing login credentials through repeated attempts. Common defense mechanisms include:
 * Limiting login attempts within a time frame (e.g., lock account or apply delay after 3 failed attempts).
 * Using CAPTCHA or generating a one-time password (OTP) sent via email/SMS to verify the user and block automated login attempts.

⚠️ Security Best Practice: Avoid revealing whether the username or password is incorrect. Always return a generic error message like "Invalid credentials" to reduce information leakage.
 */
public class PasswordPolicyValidator {
	public static final Integer MAX_PASSWORD_ATTEMPTS = 3;
    /**
     * Represents basic user details.
     */
	@lombok.Data @AllArgsConstructor @ToString
    public static class UserDetails {
        String emailId;
        String firstName, lastName;
        String groupName; // Admin, Administrator, Technical Support Group
        boolean isAdmin;
    }

	
    // Regex patterns for each required character class
    private static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[^A-Za-z0-9]");

    /**
Below are the password policies getting used currently:

It contains at least 8 characters and at most 30 characters.
It contains at least one digit.
It contains at least one upper case alphabet.
It contains at least one lower case alphabet.
It contains at least one special character which includes !@#$%&*()-+=^.
It doesn’t contain any white space
     */
    private static boolean isValidPassword(String password, String patterName, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        boolean isValid =  matcher.matches();
        
        if (isValid)
        	System.out.format("%7s [%-30S] : %b\n", patterName, password, isValid);
        else 
        	System.err.format("%7s [%-30S] : %b\n", patterName, password, isValid);
        
        return isValid;
    }

    /**
     * Returns true if password contains any prohibited substring from user's personal info.
     *
🔐 The password must NOT contain:
 * The user’s email ID (account name)
 * The first name, last name, or parts of them that exceed 2 consecutive characters
 * Any other prohibited elements like date of birth, address, phone number, etc. (customizable)

Given:
First Name: Administrator  
Last Name: Super Admin  
Email ID: admin@myapp.com  

Examples of disallowed patterns in passwords:
 * "Admin@1234" ← contains "Admin" (more than 2 chars from First Name)
 * "SuperAdmin!2025" ← contains "Super", "Admin"
 * "admin@myapp.com!" ← contains full email
 * "min123!A" ← contains "min" (3 letters from "Administrator")
 
     * @param password Password to check
     * @param user User details (first name, last name, email)
     * @return true if password contains prohibited elements, false otherwise
     */
    public static boolean containsProhibitedPatterns(String password, PasswordPolicyValidator.UserDetails user) {
        String lowerPassword = password.toLowerCase();

        // 1. Check full email ID
        if (lowerPassword.contains(user.emailId.toLowerCase())) {
            return true;
        }

        // 2. Check substrings of first/last name (>= 3 characters)
        if (hasLongSubstringMatch(lowerPassword, user.firstName)) return true;
        if (hasLongSubstringMatch(lowerPassword, user.lastName)) return true;

        // 3. Optional: add DOB, address, phone, etc. here if available
        return false;
    }

    /**
     * Helper: returns true if password contains any 3+ letter substring from the given name.
     */
    private static boolean hasLongSubstringMatch(String password, String name) {
        if (name == null || name.length() < 3) return false;

        String lowerName = name.toLowerCase();
        int len = lowerName.length();

        // Check all substrings of length 3 or more
        for (int i = 0; i <= len - 3; i++) {
            for (int j = i + 3; j <= len; j++) {
                String sub = lowerName.substring(i, j);
                if (password.contains(sub)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Validates a single password against the appropriate policy.
     *
     * @param password The password to validate
     * @param user The user whose password is being validated
     * @param isAdminUser Whether the user is privileged (admin)
     * @return true if the password meets all applicable requirements
     */
    public static boolean isPasswordValid(String password, UserDetails user, boolean isAdminUser) {
    	if (password == null || password.isBlank()) {
    		System.err.println("❌ Blank password not allowed. [Empty or blank (password length is zero ) passwords are always prohibited]");
    		return false;
    	}
    	
    	int minLength = isAdminUser ? 16 : 12;
    	int maxLength = 30;
        boolean isIncharRange = true;
        if (password.length() < minLength || password.length() < maxLength) {
        	isIncharRange = false;
        }
        System.out.println(password.length() + " chars");
        if (StringUtils.containsIgnoreCase(password, user.getFirstName())
        		|| StringUtils.containsIgnoreCase(password, user.getLastName())) {
        	System.err.println("Contains part of name: Invalid login credentials : "+user);
        }
        
        isValidPassword(password, "OLD", "^(?=.*[0-9])(?=.{8,})(?=.*[a-zA-Z])([a-zA-Z0-9!@#$%^&*-_]+)$");
        isValidPassword(password, "REGULAR", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{12,30}$");
        isValidPassword(password, "ADMIN", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{16,30}$");

        boolean hasUpper = UPPER_CASE.matcher(password).find();
        boolean hasLower = LOWER_CASE.matcher(password).find();
        boolean hasDigit = DIGIT.matcher(password).find();
        boolean hasSpecial = SPECIAL_CHAR.matcher(password).find();

        
        boolean isValid =  isIncharRange && hasUpper && hasLower && hasDigit && hasSpecial;
        
        if (isValid)
        	System.out.format("[%-30S] : %b\n", password, isValid);
        else 
        	System.err.format("[%-30S] : %b\n", password, isValid);
        
        // ❌ Reject if password contains prohibited elements
        if (containsProhibitedPatterns(password, user)) { //PasswordProhibitedCheck
        	isValid = false;
        	System.err.format("ProhibitedPattern : [%-30S] : %b\n", password, isValid);
        }
        
        if (isPassphraseValid(password)) {
        	System.out.format("🔐 Passphrase (memorable and secure password) : [%-30S] \n", password);
        }
        
        if (isPasswordBanned(password)) {
        	System.out.format("🔧 Banned Password: Do not allow passwords that are common, trivial, or easily guessable : [%-30S] \n", password);
        }
        
        return isValid;
    }

    /**
     * Validates if the input is a strong passphrase.
     *
A passphrase is a sequence of words (usually 5 or more) that forms a longer but more memorable and secure password.

Easier for humans to remember
Much harder for attackers to crack using brute-force or dictionary attacks

✅ Example: "this is a 7-word secure password!"

✔️ Criteria:
 → At least 5 words
 → Optional: minimum total length (e.g., 20+ characters)
 → Optional: include at least one uppercase, digit, or symbol (you can expand based on policy)
 
     *
     * @param passphrase The input string to check
     * @return true if it meets passphrase criteria
     */
    public static boolean isPassphraseValid(String passphrase) {
        if (passphrase == null || passphrase.trim().isEmpty()) return false;

        // Split words by whitespace
        String[] words = passphrase.trim().split("\\s+");

        // Check if it has at least 5 words
        if (words.length < 5) {
            return false;
        }

        // Optional: Enforce minimum length (e.g., 20 characters)
        if (passphrase.length() < 20) {
            return false;
        }

        return true;
    }
    /**
     * Validates a list of passwords for a privileged(admin)/regular user.
     *
     * @param user Admin/Regular user details
     * @param passwords List of passwords to validate
     */
    public static void isPasswordValids(UserDetails user, List<String> passwords) {
        System.out.println("Validating admin/regular passwords for: " + user.emailId);
        for (String password : passwords) {
            isPasswordValid(password, user, true);
        }
    }

    /**
     * Example usage and test.
     */
    public static void main(String[] args) {
        // Admin user
        UserDetails adminUser = new UserDetails("admin@myapp.com", "Administrator", "Super Admin", "Admin", true);

        //https://haveibeenpwned.com/Passwords?utm_source=chatgpt.com
        //https://github.com/synacktiv/EIPP/blob/main/eipp-global-bpl.txt
        // Test passwords
        List<String> passwords = List.of(
        		"",
        		"  ",
        		"SecureAdmin@2025", // ❌ false — contains "Admin" for User: Administrator
        		"SecureAsh77@2025", // ❌ false — contains "Ash"   for User: Yashwanth
        		"Secure#Access2025", // ✔ true — no personal info inside
        		
        		// adminPasswords
                "AdministratorPass#2024", // valid
                "StrongPass@1234", // 14 chars, upper, lower, digit, special, no name, meets 12+ rule.
                "SuperSecurePass#2024", // 18 chars, meets all complexity & length rules, no name used.
                "ValidPassphrase!Test2025", // 20 chars, meets all complexity rules, passphrase-style, no name.
                "StrongPassword1234", // No special char.
                "Admin123",         // invalid (too short, lacks special)
                "adminADMIN1234!",   // valid
                "PrivilegedUser@2025!Secure!",   // 24 chars, meets privileged 16+ char rule, all complexity satisfied.
                "CloudUser@2FA",   // 13 chars, all complexity rules met; must also enforce 2FA for external access.
                
                // regularPasswords
                "User#1234aB",       // valid
                "short1!",           // invalid (too short, missing uppercase)
                "Regular@User12",     // valid
                "Yashwanth@1234",
                "Admin@123456",
                "MyPwd@123",
                "ValidPassphrase!Test2025", 
                
                // 🔐 Passphrase (memorable and secure password)
                "This is a secure pass!2025", // 24 chars, passphrase with 5+ words, contains upper/lower/digit/special, no name, no trivial.
                "This is a 7-word secure password!",
                "secure pass with numbers 12345",
                "short phrase"
                
        		);

        // Run validations
        isPasswordValids(adminUser, passwords);
//        System.out.println();
//        isPasswordValids(adminUser2, passwords);
        
      //UserDetails adminUser2 = new UserDetails("admin@myapp.com", "Admin", "Administrator", "Administrator", true);
        // Regular user
//        UserDetails regularUser = new UserDetails("user@myapp.com", "John", "Doe", "Support", false);
//        UserDetails regularUser2 = new UserDetails("user@myapp.com", "user", "Yashwanth", "Viewer", false);
//        System.out.println();
//        isPasswordValids(regularUser, passwords);
//        System.out.println();
//        isPasswordValids(regularUser2, passwords);
    }
    
    
    
    // Static set of banned passwords (can be loaded from file/db)
    private static final Set<String> BANNED_PASSWORDS = new HashSet<>();

    static {
        // Add known weak passwords (case-insensitive check will be done)
        BANNED_PASSWORDS.add("admin");
        BANNED_PASSWORDS.add("password");
        BANNED_PASSWORDS.add("welcome");
        BANNED_PASSWORDS.add("123456");
        BANNED_PASSWORDS.add("12345678");
        BANNED_PASSWORDS.add("abc123");
        BANNED_PASSWORDS.add("qwerty");
        BANNED_PASSWORDS.add("letmein");
        BANNED_PASSWORDS.add("iloveyou");
        BANNED_PASSWORDS.add("passw0rd");
        // Add more as needed or load from external file
    }

    /**
     * Check if the password is in the banned list.
     *
     * checking against a banned password list (also known as a password denylist or blacklist).
     *blocking trivial or common passwords like "admin", "123456", "password", etc., is an essential security control.
     *
     * @param password The password to validate
     * @return true if it is a banned (trivial) password
     */
    public static boolean isPasswordBanned(String password) {
        if (password == null) return false;
        return BANNED_PASSWORDS.contains(password.toLowerCase());
    }
}
