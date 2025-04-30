import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class BankingAppGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Account currentAccount;

    public BankingAppGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels (cards)
        mainPanel.add(new WelcomePanel(), "welcome");
        mainPanel.add(new CreateAccountPanel(), "create");
        mainPanel.add(new LoginPanel(), "login");
        mainPanel.add(new AccountPanel(), "account");

        add(mainPanel);
        setTitle("Secure Banking App");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Welcome screen with options to create an account or login
    class WelcomePanel extends JPanel {
        public WelcomePanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JButton createAccountButton = new JButton("Create Account");
            JButton loginButton = new JButton("Login");
            
            createAccountButton.addActionListener(e -> cardLayout.show(mainPanel, "create"));
            loginButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(createAccountButton, gbc);
            gbc.gridy = 1;
            add(loginButton, gbc);
        }
    }

    // Panel for account creation with secure password encryption and database insertion
    class CreateAccountPanel extends JPanel {
        public CreateAccountPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JLabel userLabel = new JLabel("Username:");
            JTextField userField = new JTextField(15);
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passField = new JPasswordField(15);
            JButton createButton = new JButton("Create");
            JButton backButton = new JButton("Back");
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(userLabel, gbc);
            gbc.gridx = 1;
            add(userField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passLabel, gbc);
            gbc.gridx = 1;
            add(passField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(createButton, gbc);
            gbc.gridx = 1;
            add(backButton, gbc);
            
            createButton.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and password cannot be empty",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validate username format
                if (!AccountDAO.isValidUsername(username)) {
                    JOptionPane.showMessageDialog(this, "Username can only contain letters, numbers, and underscores",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Check if account already exists in the database
                    if (AccountDAO.getAccountByUsername(username) != null) {
                        JOptionPane.showMessageDialog(this, "Account already exists",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    byte[] salt = PasswordEncryptionService.generateSalt();
                    byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
                    Account newAccount = new Account(username, encryptedPassword, salt);
                    
                    // Store the new account in the database
                    AccountDAO.createAccount(newAccount);
                    
                    JOptionPane.showMessageDialog(this, "Account created successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    userField.setText("");
                    passField.setText("");
                    cardLayout.show(mainPanel, "welcome");
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            backButton.addActionListener(e -> {
                userField.setText("");
                passField.setText("");
                cardLayout.show(mainPanel, "welcome");
            });
        }
    }

    // Panel for secure user login; account credentials are retrieved from the database
    class LoginPanel extends JPanel {
        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JLabel userLabel = new JLabel("Username:");
            JTextField userField = new JTextField(15);
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passField = new JPasswordField(15);
            JButton loginButton = new JButton("Login");
            JButton backButton = new JButton("Back");
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(userLabel, gbc);
            gbc.gridx = 1;
            add(userField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passLabel, gbc);
            gbc.gridx = 1;
            add(passField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(loginButton, gbc);
            gbc.gridx = 1;
            add(backButton, gbc);
            
            loginButton.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());
                try {
                    if (!AccountDAO.isValidUsername(username)) {
                        JOptionPane.showMessageDialog(this, "Invalid username",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Account account = AccountDAO.getAccountByUsername(username);
                    if (account == null) {
                        JOptionPane.showMessageDialog(this, "Account does not exist",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!PasswordEncryptionService.authenticate(password, account.getEncryptedPassword(), account.getSalt())) {
                        JOptionPane.showMessageDialog(this, "Invalid credentials",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    currentAccount = account;
                    JOptionPane.showMessageDialog(this, "Login successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    userField.setText("");
                    passField.setText("");
                    ((AccountPanel) mainPanel.getComponent(3)).updateAccountInfo();
                    cardLayout.show(mainPanel, "account");
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error during authentication: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            backButton.addActionListener(e -> {
                userField.setText("");
                passField.setText("");
                cardLayout.show(mainPanel, "welcome");
            });
        }
    }

    // Panel for account operations. Updates are both local and reflected in the database.
    class AccountPanel extends JPanel {
        private JLabel welcomeLabel;
        private JLabel balanceLabel;
        private JTextField depositField;
        private JTextField withdrawField;

        public AccountPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            welcomeLabel = new JLabel("Welcome, ");
            balanceLabel = new JLabel("Balance: $0.0");
            JLabel depositLabel = new JLabel("Deposit Amount:");
            depositField = new JTextField(10);
            JButton depositButton = new JButton("Deposit");
            JLabel withdrawLabel = new JLabel("Withdraw Amount:");
            withdrawField = new JTextField(10);
            JButton withdrawButton = new JButton("Withdraw");
            JButton logoutButton = new JButton("Logout");
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(welcomeLabel, gbc);
            
            gbc.gridy = 1;
            add(balanceLabel, gbc);
            
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 2;
            add(depositLabel, gbc);
            gbc.gridx = 1;
            add(depositField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            add(depositButton, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            add(withdrawLabel, gbc);
            gbc.gridx = 1;
            add(withdrawField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 5;
            add(withdrawButton, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            add(logoutButton, gbc);
            
            depositButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(depositField.getText().trim());
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(this, "Deposit amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    currentAccount.deposit(amount);
                    AccountDAO.updateBalance(currentAccount.getUsername(), currentAccount.getBalance());
                    updateAccountInfo();
                    JOptionPane.showMessageDialog(this, "Deposited $" + amount, "Success", JOptionPane.INFORMATION_MESSAGE);
                    depositField.setText("");
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            withdrawButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(withdrawField.getText().trim());
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(this, "Withdrawal amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (currentAccount.withdraw(amount)) {
                        AccountDAO.updateBalance(currentAccount.getUsername(), currentAccount.getBalance());
                        updateAccountInfo();
                        JOptionPane.showMessageDialog(this, "Withdrew $" + amount, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    withdrawField.setText("");
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            logoutButton.addActionListener(e -> {
                depositField.setText("");
                withdrawField.setText("");
                currentAccount = null;
                cardLayout.show(mainPanel, "welcome");
            });
        }

        public void updateAccountInfo() {
            if (currentAccount != null) {
                welcomeLabel.setText("Welcome, " + currentAccount.getUsername());
                balanceLabel.setText("Balance: $" + currentAccount.getBalance());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingAppGUI());
    }
}
