package com.dish.dish.classes;

import java.util.List;
import java.util.Map;

public class MenuItem {
    private String name;
    private Map<String, String> ingredients; // JSON object
    private String portion;
    private String description;
    private Map<String, Object> nutrients; // JSON object
    private int calories;
    private String time;
    private String location;
    private List<String> allergens; // JSON array

    // Getters and setters for each field
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, String> getIngredients() { return ingredients; }
    public void setIngredients(Map<String, String> ingredients) { this.ingredients = ingredients; }
    public String getPortion() { return portion; }
    public void setPortion(String portion) { this.portion = portion; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getNutrients() { return nutrients; }
    public void setNutrients(Map<String, Object> nutrients) { this.nutrients = nutrients; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }
}
