package joke.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import joke.controller.JokeController;
import joke.dto.JokeResponseDTO;
import joke.service.JokeService;

@WebMvcTest(JokeController.class)
class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeService jokeService;

    @InjectMocks
    private JokeController jokeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jokeController).build();
    }
    
    @Test
    void givenValidCount_whenGetJokes_thenReturnsJokesList() throws Exception {
        // Given
        int count = 5;
        List<JokeResponseDTO> mockJokes = List.of(
                new JokeResponseDTO("5b045b5009f1","What's the difference between a guitar and a fish?","You can tune a guitar but you can't \\\"tuna\\\"fish!"),
                new JokeResponseDTO("e70b40dbfb14","What do I look like?","A JOKE MACHINE!?"),
                new JokeResponseDTO("e70b40dbfb15","Do you know what the word 'was' was initially?","Before was was was was was is.")
        );
        given(jokeService.getJokes(count)).willReturn(mockJokes);

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", String.valueOf(count)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].question").value("What's the difference between a guitar and a fish?"))
                .andExpect(jsonPath("$[1].question").value("What do I look like?"))
                .andExpect(jsonPath("$[2].question").value("Do you know what the word 'was' was initially?"));
    }
    
    @Test
    void givenCountLessThanOne_whenGetJokes_thenThrowsInvalidParameterException() throws Exception {
        // Given
        int invalidCount = 0;

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", String.valueOf(invalidCount)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> 
                    assertTrue(result.getResolvedException() instanceof InvalidParameterException))
                .andExpect(result -> 
                    assertEquals("Count must be between 1 and 100", result.getResolvedException().getMessage()));
    }

    @Test
    void givenCountGreaterThanHundred_whenGetJokes_thenThrowsInvalidParameterException() throws Exception {
        // Given
        int invalidCount = 101;

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", String.valueOf(invalidCount)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> 
                    assertTrue(result.getResolvedException() instanceof InvalidParameterException))
                .andExpect(result -> 
                    assertEquals("Count must be between 1 and 100", result.getResolvedException().getMessage()));
    }


    @Test
    @DisplayName("Valid Input (count = 10)")
    public void testGetJokesWithValidCount() throws Exception {
        // Given
        List<JokeResponseDTO> jokes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            JokeResponseDTO joke = new JokeResponseDTO("id-" + i, "Setup " + i, "Punchline " + i);
            jokes.add(joke);
        }

        when(jokeService.getJokes(10)).thenReturn(jokes);

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(10))
                .andExpect(jsonPath("$[0].question").value("Setup 1"))
                .andExpect(jsonPath("$[0].answer").value("Punchline 1"));
    }


    @Test
    @DisplayName("Invalid Input (count < 1)")
    public void testGetJokesWithCountLessThanOne() throws Exception {
    	// Given
        mockMvc.perform(get("/jokes")
                .param("count", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // Expect a bad request due to invalid input
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidParameterException))
                .andExpect(result -> assertEquals("Count must be between 1 and 100", result.getResolvedException().getMessage()));
    }

    @Test
    public void testGetJokes_CountLessThanOne_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/jokes")
                .param("count", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidParameterException))
                .andExpect(result -> assertEquals("Count must be between 1 and 100", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Invalid Input (count > 100)")
    public void testGetJokesWithCountGreaterThan100() throws Exception {
    	
        mockMvc.perform(get("/jokes")
                .param("count", "101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // Expect a bad request due to invalid input
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidParameterException))
                .andExpect(result -> assertEquals("Count must be between 1 and 100", result.getResolvedException().getMessage()));
    }


    @Test
    @DisplayName("JokeService returns an empty list")
    public void testGetJokesWithNoJokes() throws Exception {
    	// Given
        when(jokeService.getJokes(5)).thenReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));  // Expect an empty list
    }

    @Test
    @DisplayName("JokeService throws an exception")
    public void testJokeServiceThrowsException() throws Exception {
    	// Given
        when(jokeService.getJokes(10)).thenThrow(new RuntimeException("Service Unavailable"));

        // When & Then
        mockMvc.perform(get("/jokes")
                .param("count", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertEquals("Service Unavailable", result.getResolvedException().getMessage()));
    }
}
