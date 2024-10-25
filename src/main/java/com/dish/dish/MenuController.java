package com.dish.dish;

import com.dish.dish.classes.MenuItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @type GET
     * @return List of menu items
     */
    @GetMapping
    public List<MenuItem> getMenu(@RequestParam("location") String location) throws Exception {
        String query = "SELECT name, ingredients, portion, description, nutrients, calories, time, location, allergens FROM menuItems WHERE location = ?";
        List<MenuItem> menu = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson object mapper for JSON parsing

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, location); // Set the location parameter
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setName(rs.getString("name"));
                    item.setIngredients(objectMapper.readValue(rs.getString("ingredients"), Map.class)); // Parse ingredients JSON as Map
                    item.setPortion(rs.getString("portion"));
                    item.setDescription(rs.getString("description"));
                    item.setNutrients(objectMapper.readValue(rs.getString("nutrients"), Map.class)); // Parse nutrients JSON as Map
                    item.setCalories(rs.getInt("calories"));
                    item.setTime(rs.getString("time"));
                    item.setLocation(rs.getString("location"));
                    item.setAllergens(objectMapper.readValue(rs.getString("allergens"), List.class)); // Parse allergens JSON as List
                    menu.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // For debugging during development
            throw e;  // Consider handling this more gracefully in production
        }

        return menu;
    }



    /**
     * Get specific item from menu
     * @type GET
     * @return Specific item from menu
     * @param item - item to get
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