package joke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import joke.dto.JokeAPIResponseDTO;
import joke.dto.JokeResponseDTO;
import joke.model.Joke;
import joke.model.JokeProperties;
import joke.repository.JokeRepository;
import joke.service.JokeService;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JokeProperties jokeProperties;

    @InjectMocks
    private JokeService jokeService;

    @BeforeEach
    void setUp() {
    	  // Initialize mocks
        MockitoAnnotations.openMocks(this); // <-- Ensure mocks are initialized properly
        
        // Set mock values for jokeProperties
        when(jokeProperties.getApiUrl()).thenReturn("https://official-joke-api.appspot.com/random_joke");
        when(jokeProperties.getBatchSize()).thenReturn(10);
    }

    @Test
    public void testGetJokesSuccess() {
        // Prepare mock jokes already in the database
        Joke joke1 = new Joke();
        joke1.setSetup("Why did the chicken cross the road?");
        joke1.setPunchline("To get to the other side.");
        List<Joke> existingJokes = Arrays.asList(joke1);

        // Prepare mock API response for new jokes
        JokeAPIResponseDTO apiJoke = new JokeAPIResponseDTO();
        apiJoke.setSetup("What do you call a bear with no teeth?");
        apiJoke.setPunchline("A gummy bear.");

        // Mock repository and API calls
        when(jokeRepository.findAll()).thenReturn(existingJokes);
        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class))).thenReturn(apiJoke);

        // Call the service method
        List<JokeResponseDTO> result = jokeService.getJokes(5);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Why did the chicken cross the road?", result.get(0).getQuestion());
        assertEquals("What do you call a bear with no teeth?", result.get(1).getQuestion());

        // Verify save call
        verify(jokeRepository, times(1)).saveAll(anyList());
    }
    
    @Test
    public void testGetJokesApiFailure() {
        // Prepare mock jokes already in the database
        Joke joke1 = new Joke();
        joke1.setSetup("Why did the chicken cross the road?");
        joke1.setPunchline("To get to the other side.");
        List<Joke> existingJokes = Arrays.asList(joke1);

        // Mock repository and API calls
        when(jokeRepository.findAll()).thenReturn(existingJokes);
        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class))).thenThrow(new RestClientException("API error"));

        // Call the service method
        List<JokeResponseDTO> result = jokeService.getJokes(5);

        // Assertions
        assertEquals(1, result.size());
        assertEquals("Why did the chicken cross the road?", result.get(0).getQuestion());

        // Ensure no jokes were saved due to the API failure
        verify(jokeRepository, never()).saveAll(anyList());
    }
}
