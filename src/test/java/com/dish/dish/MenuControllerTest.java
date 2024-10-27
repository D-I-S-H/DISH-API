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
        when(resultSet.getString("ingredients")).thenReturn(
                "{\"pasta\": \"100g\", \"tomato\": \"50g\", \"olive oil\": \"10ml\"}",
                "{\"chicken breast\": \"150g\", \"whole wheat bun\": \"1\", \"lettuce\": \"20g\", \"mayonnaise\": \"5g\"}"
        );
        when(resultSet.getString("portion")).thenReturn("1 bowl", "1 sandwich");
        when(resultSet.getString("description")).thenReturn(
                "A healthy pasta salad with fresh tomatoes and olive oil.",
                "A delicious grilled chicken sandwich with lettuce and mayonnaise."
        );
        when(resultSet.getString("nutrients")).thenReturn(
                "{\"Carbs\": \"40g\", \"Protein\": \"5g\", \"Fat\": \"2g\"}",
                "{\"Carbs\": \"30g\", \"Protein\": \"25g\", \"Fat\": \"7g\"}"
        );
        when(resultSet.getInt("calories")).thenReturn(250, 350);
        when(resultSet.getString("time")).thenReturn("Lunch", "Dinner");
        when(resultSet.getString("location")).thenReturn("Main Dining Hall", "Main Dining Hall");
        when(resultSet.getString("allergens")).thenReturn("[\"gluten\", \"tomatoes\"]", "[\"gluten\", \"egg\"]");
    }

    @Test
    @WithMockUser
    void testGetMenu() throws Exception {
        mockMvc.perform(get("/menu")
                        .param("location", "Main Dining Hall"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$[0].name").value("Pasta Salad"))
                .andExpect(jsonPath("$[0].portion").value("1 bowl"))
                .andExpect(jsonPath("$[0].ingredients.pasta").value("100g"))
                .andExpect(jsonPath("$[1].name").value("Grilled Chicken Sandwich"))
                .andExpect(jsonPath("$[1].portion").value("1 sandwich"));
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
