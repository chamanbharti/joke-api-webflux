package joke;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.InvalidParameterException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import joke.controller.JokeController;
import joke.dto.JokeResponseDTO;
import joke.service.JokeService;

@WebMvcTest(JokeController.class)
class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeService jokeService;

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

}
