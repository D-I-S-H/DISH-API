package com.dish.dish;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/locations")
public class LocationsContoller {

    /**
     * Returns a list of dining hall locations
     * @type GET
     * @return List of locations
     */
    @GetMapping
    public List<String> getLocations() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Database/dish.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM locations")) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> locations = new ArrayList<>();
                while (rs.next()) {
                    locations.add(rs.getString("name"));
                }
                return locations;
            }
        }
    }
}