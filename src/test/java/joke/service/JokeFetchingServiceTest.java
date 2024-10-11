package joke.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
import joke.dto.JokeSaveDTO;
import joke.model.JokeProperties;

@ExtendWith(MockitoExtension.class)
public class JokeFetchingServiceTest {

    @InjectMocks
    private JokeFetchingService jokeFetchingService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JokeProperties jokeProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchJokesInBatches_Success() {
        
        int batchSize = 10;
        int count = 20;
        List<JokeAPIResponseDTO> mockResponse = List.of(
                new JokeAPIResponseDTO("type", "setup", "punchline"),
                new JokeAPIResponseDTO("type", "setup2", "punchline2")
        );
        JokeSaveDTO expectedDto1 = new JokeSaveDTO("type", "setup", "punchline");
        JokeSaveDTO expectedDto2 = new JokeSaveDTO("type", "setup2", "punchline2");

        when(jokeProperties.getBatchSize()).thenReturn(batchSize);
        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class)))
                .thenReturn(mockResponse.get(0))
                .thenReturn(mockResponse.get(1));

       
        List<JokeSaveDTO> jokes = jokeFetchingService.fetchJokesInBatches(count);

        assertEquals(2, jokes.size());
        assertEquals(expectedDto1.getSetup(), jokes.get(0).getSetup());
        assertEquals(expectedDto2.getSetup(), jokes.get(1).getSetup());
    }

    @Test
    public void testFetchBatch_ServiceFailure() {
        
        when(restTemplate.getForObject(anyString(), eq(JokeAPIResponseDTO.class)))
                .thenThrow(new RestClientException("API error"));

        
        List<JokeSaveDTO> jokes = jokeFetchingService.fetchJokesInBatches(1);

        assertTrue(jokes.isEmpty());
    }

    @Test
    public void testFetchJokesInBatches_InvalidBatchSize() {

        when(jokeProperties.getBatchSize()).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> jokeFetchingService.fetchJokesInBatches(5));
    }
}

