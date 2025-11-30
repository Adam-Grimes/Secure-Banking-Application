# Secure Banking Application

This project is a desktop banking application built in Java, with a primary focus on demonstrating and implementing robust, multi-layered security practices. It simulates a standard banking GUI where users can create accounts, log in, manage their balance, and perform transactions, with every action secured against common vulnerabilities.

## 🔒 Key Security Features

This application was built with a security-first mindset, incorporating the following measures:

* **Strong Password Hashing:** User passwords are **never** stored in plaintext. Passwords are hashed using **PBKDF2** (e.g., `PBKDF2WithHmacSHA256`) with a unique, randomly-generated **salt of at least 16 bytes** and a high iteration count (100,000+) to resist brute-force attacks.
* **SQL Injection Prevention:** All database queries are executed using **PreparedStatements** (parameterized queries). This ensures that user input is never directly concatenated into SQL strings, mitigating the risk of SQL injection attacks.
* **Desktop GUI Input Validation & Output Encoding:** Traditional web vulnerabilities (like XSS) are handled by strictly validating user input and escaping data before it is rendered in UI components. This prevents the injection of executable content or HTML into the Swing interface.
* **Multi-Factor Authentication (MFA):** A simulated **OTP (One-Time Password)** workflow is implemented during the login process to demonstrate multi-factor authentication concepts.
* **Secure Input Validation:** Strict validation rules are applied to usernames (regex) and passwords (enforcing minimum length, mixed cases, digits, and special characters) before account creation.
* **Secure Secrets Handling:** Cryptographic keys, database credentials, and salts are designed to be loaded from external configuration (environment variables or protected config files) rather than being hardcoded, preventing secrets leakage in source control.

---

## 🛠 Tech Stack

* **Language:** Java
* **Framework/Toolkit:** Java Swing (for the GUI)
* **Database:** MySQL (or compatible JDBC-backed relational DB)
* **Database Connectivity:** JDBC (Java Database Connectivity)
* **Security Libraries:** `java.security` and `javax.crypto` (for PBKDF2 hashing and secure random generation).

---

## ⚙️ Running the Application (high-level)

1.  **Clone the repository.**
2.  Ensure you have a compatible Java version installed (check project docs or build files for exact requirements).
3.  **Create and configure the database:**
    * Create the required schema and tables (look for any SQL files or scripts in the repo).
    * Configure DB connection settings in the application's configuration file (do **not** store production passwords in source control).
4.  Build and run the application from your IDE or using your chosen build tool.

