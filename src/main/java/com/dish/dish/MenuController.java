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
@RequestMapping("/menu")
public class MenuController {

    private final DataSource dataSource;

    @Autowired
    public MenuController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns a list of menu items for a given location
     * @param location - location to get menu items for
     * @param time - optional time to filter menu items
     * @param date - optional date to filter menu items
     * @return List of menu items
     */
    @GetMapping
    public List<MenuItem> getMenu(
            @RequestParam("location") String location,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String date) throws Exception {
        String query = "SELECT mi.name, mi.ingredients, mi.portion, mi.description, mi.nutrients, mi.calories, mi.date, mi.time, mi.location, mi.allergens, mi.labels, mi.station " +
                "FROM menuItems mi " +
                "JOIN stations s ON mi.station = s.stationName AND mi.location = s.locationName " +
                "WHERE mi.location = ?";

        // Modify query based on optional parameters
        if (time != null && date != null) {
            query += " AND mi.time = ? AND mi.date = ?";
        } else if (time != null) {
            query += " AND mi.time = ?";
        } else if (date != null) {
            query += " AND mi.date = ?";
        }

        query += " ORDER BY s.display_order";

        List<MenuItem> menu = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters
            int paramIndex = 1;
            stmt.setString(paramIndex++, location);

            if (time != null) {
                stmt.setString(paramIndex++, time);
            }

            if (date != null) {
                stmt.setString(paramIndex, date);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setName(rs.getString("name"));

                    // Parse the comma-separated ingredients into a List<String>
                    String ingredientsString = rs.getString("ingredients");
                    List<String> ingredients = Arrays.stream(ingredientsString.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    item.setIngredients(ingredients);

                    item.setPortion(rs.getString("portion"));
                    item.setDescription(rs.getString("description"));

                    // Parse nutrients as List<Map<String, String>>
                    String nutrientsJson = rs.getString("nutrients").replace("'", "\"");
                    List<Map<String, String>> nutrients = objectMapper.readValue(nutrientsJson, List.class);
                    item.setNutrients(nutrients);

                    item.setCalories(rs.getInt("calories"));
                    item.setTime(rs.getString("time"));
                    item.setLocation(rs.getString("location"));
                    item.setDate(rs.getString("date"));  // Set the date for each menu item

                    String allergensJson = rs.getString("allergens").replace("'", "\"");
                    List<String> allergens = objectMapper.readValue(allergensJson, List.class);
                    item.setAllergens(allergens);

                    String labelsJson = rs.getString("labels").replace("'", "\"");
                    List<String> labels = objectMapper.readValue(labelsJson, List.class);
                    item.setLabels(labels);

                    item.setStation(rs.getString("station"));

                    menu.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Debugging purposes
            throw e;  // TODO - Handle exception
        }
        return menu;
    }

    /**
     * Get specific item from menu
     * @param item - item to get
     * @return Specific item from menu
     */
    @GetMapping("/item")
    public String getItem(@RequestParam("item") String item) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM menuItems WHERE name = ?")) {

            stmt.setString(1, item); // Set parameter
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
                return null;
            }
        }
    }
}
