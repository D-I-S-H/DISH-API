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
    public List<MenuItem> getMenu(@RequestParam("location") String location, @RequestParam(required = false) String time) throws Exception {
        String query = "SELECT name, ingredients, portion, description, nutrients, calories, time, location, allergens FROM menuItems WHERE location = ? AND time = ?";
        if (time==null) { // If time is not specified, get all items for the location
            query = "SELECT name, ingredients, portion, description, nutrients, calories, time, location, allergens FROM menuItems WHERE location = ?";
        }
        List<MenuItem> menu = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters
            stmt.setString(1, location);
            if (time!=null) { // If time is specified, set the parameter
                stmt.setString(2, time);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setName(rs.getString("name"));
                    item.setIngredients(objectMapper.readValue(rs.getString("ingredients"), Map.class));
                    item.setPortion(rs.getString("portion"));
                    item.setDescription(rs.getString("description"));
                    item.setNutrients(objectMapper.readValue(rs.getString("nutrients"), Map.class));
                    item.setCalories(rs.getInt("calories"));
                    item.setTime(rs.getString("time"));
                    item.setLocation(rs.getString("location"));
                    item.setAllergens(objectMapper.readValue(rs.getString("allergens"), List.class));
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

    /**
     * Push rating to database
     * @type POST
     * @return Sucess
     * @param stars rating
     * @param uid user id
     * @param menuItemName the item name
     * @param menuItemLocation the location of the item
     */
    @PostMapping("Rating")
    public boolean rating(@RequestParam("Rating") int stars, int uid, String menuItemName, String menuItemLocation) throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ratings (manuItemName, menuItemLocation, accountUID, rating) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE rating = VALUES(?)")) {

            stmt.setInt(1, stars); // Set parameter
            stmt.setInt(2, uid); // Set parameter
            stmt.setInt(5, stars); // Set parameter
            stmt.setString(3, menuItemName); // Set parameter
            stmt.setString(4, menuItemLocation); // Set parameter
            try (ResultSet rs = stmt.executeQuery()) {
                
                return true;
            } 
            return false;
            
        }
    }
}