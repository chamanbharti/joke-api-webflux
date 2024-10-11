package joke.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import joke.dto.JokeSaveDTO;
import joke.model.Joke;
import joke.repository.JokeRepository;

@ExtendWith(MockitoExtension.class)
public class JokeSavingServiceTest {

    @InjectMocks
    private JokeSavingService jokeSavingService;

    @Mock
    private JokeRepository jokeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUniqueJokes_Success() {

        List<JokeSaveDTO> newJokes = List.of(
            new JokeSaveDTO("type", "setup1", "punchline1"),
            new JokeSaveDTO("type", "setup2", "punchline2")
        );
        List<Joke> existingJokes = List.of(new Joke(1L,"general","setup1", "punchline1"));

        when(jokeRepository.existsBySetup("setup1")).thenReturn(true);
        when(jokeRepository.existsBySetup("setup2")).thenReturn(false);

        List<Joke> savedJokes = jokeSavingService.saveUniqueJokes(newJokes, existingJokes);

        assertEquals(1, savedJokes.size());
        assertEquals("setup2", savedJokes.get(0).getSetup());
        verify(jokeRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testSaveUniqueJokes_NoNewJokes() {

        List<JokeSaveDTO> newJokes = List.of(
            new JokeSaveDTO("type", "setup1", "punchline1")
        );
        List<Joke> existingJokes = List.of(new Joke(1L,"general","setup1", "punchline1"));

        when(jokeRepository.existsBySetup("setup1")).thenReturn(true);

        List<Joke> savedJokes = jokeSavingService.saveUniqueJokes(newJokes, existingJokes);

        assertTrue(savedJokes.isEmpty());
        verify(jokeRepository, never()).saveAll(anyList());
    }
}

