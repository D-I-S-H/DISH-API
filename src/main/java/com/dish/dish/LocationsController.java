package com.dish.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private final DataSource dataSource;

    @Autowired
    public LocationsController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a list of dining hall locations
     * @type GET
     * @return List of locations
     */
    @GetMapping
    public List<String> getLocations() throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM locations");
             ResultSet rs = stmt.executeQuery()) {

            List<String> locations = new ArrayList<>();
            while (rs.next()) {
                locations.add(rs.getString("name"));
            }
            return locations;
        }
    }
}
