package com.dish.dish.classes;

import java.util.List;
import java.util.Map;

public class MenuItem {
    private String name;
    private List<String> ingredients;
    private String portion;
    private String description;
    private List<Map<String, String>> nutrients;
    private int calories;
    private String time;
    private String location;
    private String date;
    private List<String> allergens;
    private List<String> labels;
    private String station;

    // Getters and setters for each field
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public String getPortion() { return portion; }
    public void setPortion(String portion) { this.portion = portion; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Map<String, String>> getNutrients() { return nutrients; }
    public void setNutrients(List<Map<String, String>> nutrients) { this.nutrients = nutrients; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }

}
