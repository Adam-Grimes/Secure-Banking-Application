import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {
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
        return null;
    }

    public static void createAccount(Account account) throws SQLException {
        String query = "INSERT INTO accounts (username, encrypted_password, salt, balance, csrf_token) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            stmt.setString(1, account.getUsername());
            stmt.setBytes(2, account.getEncryptedPassword());
            stmt.setBytes(3, account.getSalt());
            stmt.setDouble(4, account.getBalance());
            stmt.setString(5, null);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public static void updateBalance(String username, double balance, String csrfToken) throws SQLException {
        String query = "UPDATE accounts SET balance = ? WHERE username = ? AND csrf_token = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false);
            stmt.setDouble(1, balance);
            stmt.setString(2, username);
            stmt.setString(3, csrfToken);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update failed - invalid CSRF token");
            }
            conn.commit();
        }
    }

    public static void updateCsrfToken(String username, String csrfToken) throws SQLException {
        String query = "UPDATE accounts SET csrf_token = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, csrfToken);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
}