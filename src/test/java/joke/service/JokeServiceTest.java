package joke.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import joke.dto.JokeAPIResponseDTO;
import joke.exception.InvalidJokeResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import joke.dto.JokeResponseDTO;
import joke.dto.JokeSaveDTO;
import joke.model.Joke;
import joke.repository.JokeRepository;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {

    @InjectMocks
    private JokeService jokeService;

    @Mock
    private JokeFetchingService jokeFetchingService;

    @Mock
    private JokeSavingService jokeSavingService;

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetJokes_Success() {
     
        int count = 2;
        List<Joke> existingJokes = List.of(
            new Joke(1L,"general","setup1", "punchline1"),
            new Joke(2L,"general","setup2", "punchline2")
        );
        List<JokeSaveDTO> newJokes = List.of(
            new JokeSaveDTO("type", "setup3", "punchline3")
        );
        List<Joke> savedJokes = List.of(new Joke(3L,"general","setup3", "punchline3"));

        when(jokeRepository.findAll()).thenReturn(existingJokes);
        when(jokeFetchingService.fetchJokesInBatches(anyInt())).thenReturn(newJokes);
        when(jokeSavingService.saveUniqueJokes(anyList(), anyList())).thenReturn(savedJokes);

        List<JokeResponseDTO> jokes = jokeService.getJokes(count);

        assertEquals(2, jokes.size());
        verify(jokeFetchingService, times(1)).fetchJokesInBatches(anyInt());
        verify(jokeSavingService, times(1)).saveUniqueJokes(anyList(), anyList());
    }

    @Test
    public void testGetJokes_NoNewJokesNeeded() {
        int count = 2;
        List<Joke> existingJokes = List.of(
            new Joke(1L,"general","setup1", "punchline1"),
            new Joke(2L,"general","setup2", "punchline2")
        );

        when(jokeRepository.findAll()).thenReturn(existingJokes);

        List<JokeResponseDTO> jokes = jokeService.getJokes(count);

        assertEquals(2, jokes.size());
        verify(jokeFetchingService, never()).fetchJokesInBatches(anyInt());
        verify(jokeSavingService, never()).saveUniqueJokes(anyList(), anyList());
    }
    
    @Test
    public void testGetJokes_CountLessThanOne_ShouldThrowException() {

        int invalidCount = -1;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jokeService.getJokes(invalidCount);
        });

        assertEquals("Count must be between 1 and 100", exception.getMessage());
    }

    @Test
    public void testGetJokes_CountMoreThan100_ShouldThrowException() {

        int invalidCount = 101;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jokeService.getJokes(invalidCount);
        });

        assertEquals("Count must be between 1 and 100", exception.getMessage());
    }

    @Test
    public void testGetJokes_ValidCount_ShouldReturnJokes() {
        int validCount = 5;
        List<Joke> existingJokes = List.of(
            new Joke(1L,"general","setup1", "punchline1"),
            new Joke(2L,"general","setup2", "punchline2")
        );

        when(jokeRepository.findAll()).thenReturn(existingJokes);
        when(jokeFetchingService.fetchJokesInBatches(anyInt())).thenReturn(Collections.emptyList());
        when(jokeSavingService.saveUniqueJokes(anyList(), anyList())).thenReturn(Collections.emptyList());

        List<JokeResponseDTO> jokes = jokeService.getJokes(validCount);

        assertEquals(2, jokes.size());
        verify(jokeRepository, times(1)).findAll();
        verify(jokeFetchingService, never()).fetchJokesInBatches(anyInt());
        verify(jokeSavingService, never()).saveUniqueJokes(anyList(), anyList());
    }

    @Test
    public void testGetJokes_ApiResponseInvalid_ShouldThrowInvalidJokeResponseException() {

        int validCount = 5;
        when(jokeRepository.findAll()).thenReturn(Collections.emptyList());
        when(jokeFetchingService.fetchJokesInBatches(anyInt())).thenReturn(Collections.emptyList());

        // Simulate an invalid response from the joke API
        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class)))
                .thenReturn(null); // Simulating null response

        Exception exception = assertThrows(InvalidJokeResponseException.class, () -> {
            jokeService.getJokes(validCount);
        });

        assertEquals("Received invalid joke response from API", exception.getMessage());
    }

    @Test
    public void testGetJokes_ApiResponseHasNullFields_ShouldThrowInvalidJokeResponseException() {

        int validCount = 5;
        when(jokeRepository.findAll()).thenReturn(Collections.emptyList());
        when(jokeFetchingService.fetchJokesInBatches(anyInt())).thenReturn(Collections.emptyList());

        // Simulate an invalid joke API response
        JokeAPIResponseDTO invalidApiResponse = new JokeAPIResponseDTO();
        invalidApiResponse.setSetup(null); // Simulating null setup field
        invalidApiResponse.setPunchline("Some punchline");

        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class)))
                .thenReturn(invalidApiResponse);


        Exception exception = assertThrows(InvalidJokeResponseException.class, () -> {
            jokeService.getJokes(validCount);
        });

        assertEquals("Received invalid joke response from API", exception.getMessage());
    }
}

