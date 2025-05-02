import java.security.SecureRandom;

public class SecurityUtils {

    // Escapes HTML characters to prevent XSS attacks
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;") 
                    .replace("<", "&lt;") 
                    .replace(">", "&gt;") 
                    .replace("\"", "&quot;") 
                    .replace("'", "&#39;"); 
    }

    // Generates a random CSRF token
    public static String generateCsrfToken() {
        SecureRandom random = new SecureRandom(); // Secure random number generator
        byte[] bytes = new byte[16]; // 16-byte array for the token
        random.nextBytes(bytes); // Fill the array with random bytes
        return bytesToHex(bytes); // Convert the bytes to a hexadecimal string
    }

    // Converts a byte array to a hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(); // StringBuilder for efficient string concatenation
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // Format each byte as a two-digit hexadecimal number
        }
        return sb.toString(); // Return the resulting hexadecimal string
    }

    // Validates a one-time password (OTP)
    public static boolean validateOTP(String userOTP) {
        // Simulated validation: correct OTP is "123456"
        return userOTP != null && userOTP.equals("123456"); // Check if the provided OTP matches the expected value
    }

    // Validates if a password meets the required criteria
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // Password must be at least 8 characters long
        }
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isLowerCase(c)) hasLowercase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar; // All conditions must be met
    }
}