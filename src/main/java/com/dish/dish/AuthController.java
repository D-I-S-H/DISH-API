package com.dish.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
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
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) throws Exception {
        System.out.println("Username: " + username);
        // Connect to the database
        Connection conn = DriverManager.getConnection("jdbc:sqlite:Database/dish.db");

        // retrieve user
        PreparedStatement stmt = conn.prepareStatement("SELECT password, username, uid FROM accounts WHERE username = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery(); // Execute the query

        if (rs.next()) { // If user found
            String storedHashedPassword = rs.getString("password"); // Retrieve the stored hashed password

            // compare passwords
            if (verifyPassword(password, storedHashedPassword)) { // If password matches
                String foundUsername = rs.getString("username");
                int uid = rs.getInt("uid");

                // Create response data
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", foundUsername);
                userData.put("uid", uid);

                return new ResponseEntity<>(userData, HttpStatus.OK); // Return success with 200 OK
            } else { // If password doesn't match
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return unauthorized
            }
        } else { // User not found
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Return unauthorized
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
    public ResponseEntity<Map<String, Object>> register(@RequestParam String username, @RequestParam String password) throws Exception {
        // Connect to the database
        Connection conn = DriverManager.getConnection("jdbc:sqlite:Database/dish.db");

        // Check if the username already exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM accounts WHERE username = ?");
        checkStmt.setString(1, username);
        ResultSet checkRs = checkStmt.executeQuery();

        if (checkRs.next()) { // Username already exists, return conflict
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Return 409 Conflict
        }



        // Hash password
        String hashedPassword = hashPassword(password);

        // Store new user
        PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO accounts (username, password) VALUES (?, ?)"
        );
        insertStmt.setString(1, username);
        insertStmt.setString(2, hashedPassword); // Store hashed password

        insertStmt.executeUpdate(); // Execute the insert

        // Create response data
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);

        // Retrieve the uid of the newly created user
        PreparedStatement uidStmt = conn.prepareStatement("SELECT uid FROM accounts WHERE username = ?");
        uidStmt.setString(1, username);
        ResultSet uidRs = uidStmt.executeQuery();
        if (uidRs.next()) {
            int uid = uidRs.getInt("uid");
            userData.put("uid", uid);
        }

        return new ResponseEntity<>(userData, HttpStatus.CREATED); // Return 201 Created
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

}

