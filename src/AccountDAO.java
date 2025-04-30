import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    // Retrieve an account by username
    public static Account getAccountByUsername(String username) throws SQLException {
        String query = "SELECT username, encrypted_password, salt, balance FROM accounts WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getString("username"),
                        rs.getBytes("encrypted_password"),
                        rs.getBytes("salt"),
                        rs.getDouble("balance")
                    );
                }
            }
        }
        return null; // No account found
    }

    // Create a new account in the database using a transaction
    public static void createAccount(Account account) throws SQLException {
        String query = "INSERT INTO accounts (username, encrypted_password, salt, balance) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false); // Start transaction
            
            stmt.setString(1, account.getUsername());
            stmt.setBytes(2, account.getEncryptedPassword());
            stmt.setBytes(3, account.getSalt());
            stmt.setDouble(4, account.getBalance());

            stmt.executeUpdate();
            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            throw new SQLException("Error creating account: " + e.getMessage(), e);
        }
    }

    // Update an account's balance using a transaction
    public static void updateBalance(String username, double balance) throws SQLException {
        String query = "UPDATE accounts SET balance = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            conn.setAutoCommit(false); // Start transaction

            stmt.setDouble(1, balance);
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating balance failed, no rows affected.");
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            throw new SQLException("Error updating balance: " + e.getMessage(), e);
        }
    }
    
    public static boolean isValidUsername(String username) {
    // Ensure username is not empty and only contains alphanumeric characters
    return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
}

}
