public class Account {
    private final String username;
    private final byte[] encryptedPassword;
    private final byte[] salt;
    private double balance;
    private String csrfToken;

    public Account(String username, byte[] encryptedPassword, byte[] salt) {
        this(username, encryptedPassword, salt, 0.0);
    }

    public Account(String username, byte[] encryptedPassword, byte[] salt, double balance) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.salt = salt;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public double getBalance() {
        return balance;
    }


    public String getCsrfToken() {
        return csrfToken; 
    }
    
    // Setters
    public void setCsrfToken(String csrfToken) { 
        this.csrfToken = csrfToken; 
    }

    // Transaction methods
    public void deposit(double amount) { balance += amount; }
    
    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
    

    
