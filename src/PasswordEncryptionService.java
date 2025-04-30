import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordEncryptionService {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Create strings for the password and the attempted password
        String password = "mySecurePassword";
        String attemptedPassword = "mySecurePassword"; // For testing, this matches the original

        // Get the salt
        byte[] salt = generateSalt();

        // Encrypt the password
        byte[] encryptedPassword = getEncryptedPassword(password, salt);

        // Validate the password
        boolean matched = authenticate(attemptedPassword, encryptedPassword, salt);

        // Print out the password, the attempted password, the salt, and whether they match or not.
        System.out.println("Original password: " + password);
        System.out.println("Attempted password: " + attemptedPassword);
        System.out.println("Salt: " + Arrays.toString(salt));
        System.out.println("Encrypted password: " + Arrays.toString(encryptedPassword));
        System.out.println("Password match: " + matched);
    }

    // Generates an 8-byte (64-bit) salt as recommended by RSA PKCS5 using a secure random number generator.
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        // It's very important to use SecureRandom instead of just Random.
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8]; // 8 bytes (64 bits) of salt
        random.nextBytes(salt);
        return salt;
    }

    // Authenticates the attempted password by comparing its encrypted form with the stored encrypted password.
    public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Encrypt the clear-text password using the same salt that was used for the original password.
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

        // Authentication succeeds if the encrypted attempted password equals the stored encrypted password.
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    // Encrypts a password using PBKDF2 with HMAC-SHA1.
    public static byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // PBKDF2 with SHA-1 is used as the hashing algorithm.
        String algorithm = "PBKDF2WithHmacSHA1";

        // SHA-1 generates 160-bit hashes.
        int derivedKeyLength = 160;

        // Iteration count: higher counts (e.g., 20000) make brute-force attacks harder.
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }
}
