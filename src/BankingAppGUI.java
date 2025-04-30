import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class BankingAppGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Account currentAccount;

    public BankingAppGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

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
                
                try {
                    if (!AccountDAO.isValidUsername(username)) {
                        JOptionPane.showMessageDialog(this, "Invalid username format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (AccountDAO.getAccountByUsername(username) != null) {
                        JOptionPane.showMessageDialog(this, "Account already exists",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    byte[] salt = PasswordEncryptionService.generateSalt();
                    byte[] encryptedPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
                    Account newAccount = new Account(username, encryptedPassword, salt);
                    AccountDAO.createAccount(newAccount);
                    
                    JOptionPane.showMessageDialog(this, "Account created successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    userField.setText("");
                    passField.setText("");
                    cardLayout.show(mainPanel, "welcome");
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating account",
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
                    Account account = AccountDAO.getAccountByUsername(username);
                    if (account == null) {
                        JOptionPane.showMessageDialog(this, "Account not found",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (!PasswordEncryptionService.authenticate(password, 
                            account.getEncryptedPassword(), account.getSalt())) {
                        JOptionPane.showMessageDialog(this, "Invalid credentials",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Generate and store new CSRF token
                    String csrfToken = SecurityUtils.generateCsrfToken();
                    AccountDAO.updateCsrfToken(account.getUsername(), csrfToken);
                    account.setCsrfToken(csrfToken);
                    
                    currentAccount = account;
                    ((AccountPanel) mainPanel.getComponent(3)).updateAccountInfo();
                    userField.setText("");
                    passField.setText("");
                    cardLayout.show(mainPanel, "account");
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Login failed",
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

    class AccountPanel extends JPanel {
        private JLabel welcomeLabel = new JLabel("Welcome, ");
        private JLabel balanceLabel = new JLabel("Balance: €0.0");
        private JTextField depositField = new JTextField(10);
        private JTextField withdrawField = new JTextField(10);

        public AccountPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JButton depositButton = new JButton("Deposit");
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
            add(new JLabel("Deposit Amount:"), gbc);
            gbc.gridx = 1;
            add(depositField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            add(depositButton, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            add(new JLabel("Withdraw Amount:"), gbc);
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
                    double amount = Double.parseDouble(depositField.getText());
                    if (amount <= 0) throw new NumberFormatException();
                    
                    currentAccount.deposit(amount);
                    AccountDAO.updateBalance(currentAccount.getUsername(), 
                            currentAccount.getBalance(), 
                            currentAccount.getCsrfToken());
                    updateAccountInfo();
                    depositField.setText("");
                    JOptionPane.showMessageDialog(this, "Deposit successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid deposit amount",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            withdrawButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(withdrawField.getText());
                    if (amount <= 0) throw new NumberFormatException();
                    
                    if (currentAccount.withdraw(amount)) {
                        AccountDAO.updateBalance(currentAccount.getUsername(), 
                                currentAccount.getBalance(), 
                                currentAccount.getCsrfToken());
                        updateAccountInfo();
                        withdrawField.setText("");
                        JOptionPane.showMessageDialog(this, "Withdrawal successful!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient funds",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid withdrawal amount",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            logoutButton.addActionListener(e -> {
                currentAccount = null;
                depositField.setText("");
                withdrawField.setText("");
                cardLayout.show(mainPanel, "welcome");
            });
        }

        public void updateAccountInfo() {
            if (currentAccount != null) {
                String safeUsername = SecurityUtils.escapeHtml(currentAccount.getUsername());
                welcomeLabel.setText("Welcome, " + safeUsername);
                balanceLabel.setText("Balance: €" + currentAccount.getBalance());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingAppGUI());
    }
}