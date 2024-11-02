package com.dish.dish;

import com.dish.dish.classes.MenuItem;
import com.dish.dish.classes.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
@Import(TestSecurityConfig.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource; // Mock the DataSource

    @MockBean
    private Connection connection;

    @MockBean
    private PreparedStatement preparedStatement;

    @MockBean
    private ResultSet resultSet;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        // Mock the DataSource to return the mocked connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock the PreparedStatement and ResultSet for getMenu method
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock the ResultSet for multiple menu items
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("name")).thenReturn("Pasta Salad", "Grilled Chicken Sandwich");

        // Ingredients as a comma-separated string to match schema
        when(resultSet.getString("ingredients")).thenReturn(
                "pasta, tomato, olive oil",
                "chicken breast, whole wheat bun, lettuce, mayonnaise"
        );

        when(resultSet.getString("portion")).thenReturn("1 bowl", "1 sandwich");
        when(resultSet.getString("description")).thenReturn(
                "A healthy pasta salad with fresh tomatoes and olive oil.",
                "A delicious grilled chicken sandwich with lettuce and mayonnaise."
        );

        // Nutrients as JSON array of objects
        when(resultSet.getString("nutrients")).thenReturn(
                "[{'name': 'Carbs', 'value': '40g', 'uom': 'g'}, {'name': 'Protein', 'value': '5g', 'uom': 'g'}, {'name': 'Fat', 'value': '2g', 'uom': 'g'}]",
                "[{'name': 'Carbs', 'value': '30g', 'uom': 'g'}, {'name': 'Protein', 'value': '25g', 'uom': 'g'}, {'name': 'Fat', 'value': '7g', 'uom': 'g'}]"
        );

        when(resultSet.getInt("calories")).thenReturn(250, 350);
        when(resultSet.getString("time")).thenReturn("Lunch", "Dinner");
        when(resultSet.getString("location")).thenReturn("Main Dining Hall", "Main Dining Hall");

        // Allergens as JSON array
        when(resultSet.getString("allergens")).thenReturn("[\"gluten\", \"tomatoes\"]", "[\"gluten\", \"egg\"]");

        // Labels as JSON array
        when(resultSet.getString("labels")).thenReturn("[\"vegan\", \"low-calorie\"]", "[\"high-protein\"]");
    }

    @Test
    @WithMockUser
    void testGetMenu() throws Exception {
        mockMvc.perform(get("/menu")
                        .param("location", "Main Dining Hall"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$[0].name").value("Pasta Salad"))
                .andExpect(jsonPath("$[0].portion").value("1 bowl"))
                .andExpect(jsonPath("$[0].ingredients[0]").value("pasta"))
                .andExpect(jsonPath("$[0].ingredients[1]").value("tomato"))
                .andExpect(jsonPath("$[0].ingredients[2]").value("olive oil"))
                .andExpect(jsonPath("$[0].description").value("A healthy pasta salad with fresh tomatoes and olive oil."))
                .andExpect(jsonPath("$[0].nutrients[0].name").value("Carbs"))
                .andExpect(jsonPath("$[0].nutrients[0].value").value("40g"))
                .andExpect(jsonPath("$[0].calories").value(250))
                .andExpect(jsonPath("$[0].time").value("Lunch"))
                .andExpect(jsonPath("$[0].location").value("Main Dining Hall"))
                .andExpect(jsonPath("$[0].allergens[0]").value("gluten"))
                .andExpect(jsonPath("$[0].allergens[1]").value("tomatoes"))
                .andExpect(jsonPath("$[0].labels[0]").value("vegan"))
                .andExpect(jsonPath("$[0].labels[1]").value("low-calorie"))

                .andExpect(jsonPath("$[1].name").value("Grilled Chicken Sandwich"))
                .andExpect(jsonPath("$[1].portion").value("1 sandwich"))
                .andExpect(jsonPath("$[1].ingredients[0]").value("chicken breast"))
                .andExpect(jsonPath("$[1].ingredients[1]").value("whole wheat bun"))
                .andExpect(jsonPath("$[1].ingredients[2]").value("lettuce"))
                .andExpect(jsonPath("$[1].ingredients[3]").value("mayonnaise"))
                .andExpect(jsonPath("$[1].description").value("A delicious grilled chicken sandwich with lettuce and mayonnaise."))
                .andExpect(jsonPath("$[1].nutrients[1].name").value("Protein"))
                .andExpect(jsonPath("$[1].nutrients[1].value").value("25g"))
                .andExpect(jsonPath("$[1].calories").value(350))
                .andExpect(jsonPath("$[1].time").value("Dinner"))
                .andExpect(jsonPath("$[1].location").value("Main Dining Hall"))
                .andExpect(jsonPath("$[1].allergens[0]").value("gluten"))
                .andExpect(jsonPath("$[1].allergens[1]").value("egg"))
                .andExpect(jsonPath("$[1].labels[0]").value("high-protein"));
    }

    @Test
    @WithMockUser
    void testGetItem() throws Exception {
        // Setup mock for specific item retrieval
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("name")).thenReturn("Pasta Salad");

        mockMvc.perform(get("/menu/item")
                        .param("item", "Pasta Salad"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$").value("Pasta Salad"));
    }
}
