public class Account {
    private String username;
    private byte[] encryptedPassword;
    private byte[] salt;
    private double balance;

    // Constructor used when creating a new account (balance starts at 0)
    public Account(String username, byte[] encryptedPassword, byte[] salt) {
        this(username, encryptedPassword, salt, 0.0);
    }
    
    // Constructor used when loading an account from the database
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
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
