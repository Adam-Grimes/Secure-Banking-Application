import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    // Retrieves an account by username from the database
    public static Account getAccountByUsername(String username) throws SQLException {
        String query = "SELECT username, encrypted_password, salt, balance, csrf_token FROM accounts WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                        rs.getString("username"),
                        rs.getBytes("encrypted_password"),
                        rs.getBytes("salt"),
                        rs.getDouble("balance")
                    );
                    account.setCsrfToken(rs.getString("csrf_token"));
                    return account;
                }
            }
        }
        return null; // Returns null if no account is found
    }

    // Creates a new account in the database
    public static void createAccount(Account account) throws SQLException {
        String query = "INSERT INTO accounts (username, encrypted_password, salt, balance, csrf_token) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false); // Begin transaction
            stmt.setString(1, account.getUsername());
            stmt.setBytes(2, account.getEncryptedPassword());
            stmt.setBytes(3, account.getSalt());
            stmt.setDouble(4, account.getBalance());
            stmt.setString(5, null); // CSRF token is initially null
            stmt.executeUpdate();
            conn.commit(); // Commit transaction
        }
    }

    // Updates the balance of an account, ensuring CSRF token validation
    public static void updateBalance(String username, double balance, String csrfToken) throws SQLException {
        String query = "UPDATE accounts SET balance = ? WHERE username = ? AND csrf_token = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false); // Begin transaction
            stmt.setDouble(1, balance);
            stmt.setString(2, username);
            stmt.setString(3, csrfToken);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update failed - invalid CSRF token"); // Ensures CSRF token is valid
            }
            conn.commit(); // Commit transaction
        }
    }

    // Updates the CSRF token for a specific account
    public static void updateCsrfToken(String username, String csrfToken) throws SQLException {
        String query = "UPDATE accounts SET csrf_token = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, csrfToken);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    // Validates the format of a username
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$"); // Ensures username is alphanumeric and 3-20 characters long
    }
}