public class Account {
    // Fields to store account details
    private final String username; // Username of the account
    private final byte[] encryptedPassword; // Encrypted password for security
    private final byte[] salt; // Salt used for password encryption
    private double balance; // Account balance
    private String csrfToken; // CSRF token for security

    // Constructor for creating an account with a default balance of 0.0
    public Account(String username, byte[] encryptedPassword, byte[] salt) {
        this(username, encryptedPassword, salt, 0.0);
    }

    // Constructor for creating an account with a specified balance
    public Account(String username, byte[] encryptedPassword, byte[] salt, double balance) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.salt = salt;
        this.balance = balance;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for encrypted password
    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    // Getter for salt
    public byte[] getSalt() {
        return salt;
    }

    // Getter for account balance
    public double getBalance() {
        return balance;
    }

    // Getter for CSRF token
    public String getCsrfToken() {
        return csrfToken; 
    }
    
    // Setter for CSRF token
    public void setCsrfToken(String csrfToken) { 
        this.csrfToken = csrfToken; 
    }

    // Method to deposit money into the account
    public void deposit(double amount) { 
        balance += amount; 
    }
    
    // Method to withdraw money from the account
    public boolean withdraw(double amount) {
        if (balance >= amount) { // Check if sufficient balance is available
            balance -= amount;
            return true; // Withdrawal successful
        }
        return false; // Withdrawal failed due to insufficient funds
    }
}



