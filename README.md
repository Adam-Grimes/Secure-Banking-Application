# Secure Banking Application

This project is a desktop banking application built in Java, with a primary focus on demonstrating and implementing robust, multi-layered security practices. It simulates a standard banking GUI where users can create accounts, log in, manage their balance, and perform transactions, with every action secured against common vulnerabilities.

## 🔒 Key Security Features

This application was built with a security-first mindset, incorporating the following measures:

* **Password Hashing:** User passwords are **never** stored in plaintext. All passwords are encrypted using **PBKDF2WithHmacSHA1** with a unique, randomly-generated 8-byte **salt** for each user.
* **SQL Injection Prevention:** All database queries are executed using **PreparedStatements**. This ensures that user input is never directly concatenated into SQL strings, mitigating the risk of SQL injection attacks.
* **Cross-Site Request Forgery (CSRF) Protection:** All sensitive actions (like deposits and withdrawals) are authorized using a unique **CSRF token** that is generated at login and validated with every transaction.
* **Cross-Site Scripting (XSS) Prevention:** All user-provided data (like usernames) is sanitized using **HTML escaping** before being displayed in the GUI, preventing malicious scripts from being rendered.
* **Multi-Factor Authentication (MFA):** A simulated **OTP (One-Time Password)** check is implemented during the login process to demonstrate a multi-factor authentication workflow.
* **Secure Input Validation:** Strict validation rules are applied to usernames (regex) and passwords (checking for length, case, numbers, and special characters) before account creation.

---

## 🛠 Tech Stack

* **Language:** Java
* **Framework/Toolkit:** Java Swing (for the GUI)
* **Database:** MySQL
* **Database Connectivity:** JDBC (Java Database Connectivity)
* **Security Libraries:** `java.security` and `javax.crypto` for hashing and salt generation.
