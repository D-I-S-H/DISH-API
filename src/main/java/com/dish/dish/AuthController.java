package com.dish.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Password encoder
    private final SecureRandom secureRandom = new SecureRandom(); // For generating random tokens

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    /**
     * Returns login information for the user
     * @path /auth/login
     * @type POST
     * @return User object
     * @param username - username of user
     * @param password - password of user
     * @throws Exception
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) throws Exception {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Database/dish.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT password, username, uid, token FROM accounts WHERE username = ?")) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) { // Execute the query and automatically close ResultSet
                if (rs.next()) { // If user found
                    String storedHashedPassword = rs.getString("password"); // Retrieve the stored hashed password

                    // compare passwords
                    if (verifyPassword(password, storedHashedPassword)) { // If password matches
                        String foundUsername = rs.getString("username");
                        int uid = rs.getInt("uid");
                        String token = rs.getString("token");

                        // Create response data
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("username", foundUsername);
                        userData.put("uid", uid);
                        userData.put("token", token);

                        return new ResponseEntity<>(userData, HttpStatus.OK); // Return success with 200 OK
                    } else { // If password doesn't match
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return unauthorized
                    }
                } else { // User not found
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return unauthorized
                }
            }
        }
    }

    /**
     * Registers a new user
     * @path /auth/register
     * @type POST
     * @return User object
     * @param username - username of user
     * @param password - password of user
     * @throws Exception
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) throws Exception {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");

        if (!username.matches("[a-zA-Z0-9]+")) { // Regex to check for alphanumeric characters
            Map<String, Object> errorData = new HashMap<>();
            return new ResponseEntity<>(errorData, HttpStatus.BAD_REQUEST); // Return 400 Bad Request
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Database/dish.db");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM accounts WHERE username = ?")) {

            checkStmt.setString(1, username);
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) { // Username already exists, return conflict
                    return new ResponseEntity<>(HttpStatus.CONFLICT); // Return 409 Conflict
                }
            }

            // Hash password and generate token
            String hashedPassword = hashPassword(password);
            String token = generateToken();

            // Store user
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO accounts (username, password, token) VALUES (?, ?, ?)")) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword); // Store hashed password
                insertStmt.setString(3, token); // Store generated token
                insertStmt.executeUpdate(); // Execute the insert
            }

            // Create response data
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("token", token);

            // Retrieve the uid of the newly created user
            try (PreparedStatement uidStmt = conn.prepareStatement("SELECT uid FROM accounts WHERE username = ?")) {
                uidStmt.setString(1, username);
                try (ResultSet uidRs = uidStmt.executeQuery()) {
                    if (uidRs.next()) {
                        int uid = uidRs.getInt("uid");
                        userData.put("uid", uid);
                    }
                }
            }

            return new ResponseEntity<>(userData, HttpStatus.CREATED); // Return 201 Created
        }
    }

    /**
     * Hashes the given password
     * @param password - password to hash
     * @return hashed password
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password); // BCrypt automatically generates and stores the salt
    }

    /**
     * Compares the raw password with the encoded password
     * @param rawPassword - input password
     * @param encodedPassword - stored password
     * @return true if the passwords match, false otherwise
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Generates a random 32-character hex token
     * @return random token
     */
    private String generateToken() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        StringBuilder token = new StringBuilder();
        for (byte b : bytes) {
            token.append(String.format("%02x", b));
        }
        return token.toString();
    }
}
