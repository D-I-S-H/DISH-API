package com.dish.dish;

import com.dish.dish.classes.MenuItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rating")
public class RatingController {

    private final DataSource dataSource;

    @Autowired
    public RatingController(DataSource dataSource) {
        this.dataSource = dataSource;
    }
/**
     * Push rating to database
     * @return Sucess
     * @param stars rating
     * @param uid user id
     * @param menuItemName the item name
     * @param menuItemLocation the location of the item
     */
    @PostMapping("Rating")
    public void rating(@RequestParam("Rating") int stars, int uid, String menuItemName, String menuItemLocation) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ratings (manuItemName, menuItemLocation, accountUID, rating) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE rating = VALUES(rating)")) {

            stmt.setInt(1, stars); // Set parameter
            stmt.setInt(2, uid); // Set parameter
            stmt.setString(3, menuItemName); // Set parameter
            stmt.setString(4, menuItemLocation); // Set parameter
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return ResponseEntity.ok("Rating successfully submitted!");
            } else {
                return ResponseEntity.status(500).body("Failed to submit rating.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while submitting the rating: " + e.getMessage());
        }
            
        }
    }
}