package com.dish.dish;

import com.dish.dish.classes.TestSecurityConfig;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationsController.class)
@Import(TestSecurityConfig.class) // Disable security in tests
public class LocationsControllerTest {

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

    @BeforeEach
    public void setup() throws Exception {
        // Mock the DataSource to return the mocked connection
        when(dataSource.getConnection()).thenReturn(connection);

        // Mock the behavior of the PreparedStatement and ResultSet
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock the ResultSet
        when(resultSet.next()).thenReturn(true, true, true, false); // 3 records, then stop
        when(resultSet.getString("name")).thenReturn("DHH", "Wadsworth", "Cafe");
    }

    @Test
    @WithMockUser // To bypass security
    void testGetLocations() throws Exception {
        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(content().json("[\"DHH\", \"Wadsworth\", \"Cafe\"]")); // Check that the returned JSON matches
    }
}
