package joke.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import joke.dto.JokeResponseDTO;
import joke.dto.JokeSaveDTO;
import joke.model.Joke;
import joke.repository.JokeRepository;

@Service
public class JokeService {

    private final JokeFetchingService jokeFetchingService;
    private final JokeSavingService jokeSavingService;
    private final JokeRepository jokeRepository;

    @Autowired
    public JokeService(JokeFetchingService jokeFetchingService, JokeSavingService jokeSavingService, JokeRepository jokeRepository) {
        this.jokeFetchingService = jokeFetchingService;
        this.jokeSavingService = jokeSavingService;
        this.jokeRepository = jokeRepository;
    }

    public List<JokeResponseDTO> getJokes(int count) {

        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("Count must be between 1 and 100");
        }
        // Retrieve all existing jokes from the database
        List<Joke> existingJokes = jokeRepository.findAll();

        // Calculate how many jokes are needed
        int remainingJokesNeeded = count - existingJokes.size();

        if (remainingJokesNeeded > 0) {
            // Fetch and save unique jokes
            List<JokeSaveDTO> newJokes = jokeFetchingService.fetchJokesInBatches(remainingJokesNeeded);
            List<Joke> savedJokes = jokeSavingService.saveUniqueJokes(newJokes, existingJokes);

            // Add the saved jokes to the existing ones
            existingJokes.addAll(savedJokes);
        }

        // Convert jokes to JokeResponseDTO and limit to requested count
        return existingJokes.stream()
                .limit(count)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Joke entity to a JokeResponseDTO.
     */
    private JokeResponseDTO toResponseDTO(Joke joke) {
        JokeResponseDTO dto = new JokeResponseDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setQuestion(joke.getSetup());
        dto.setAnswer(joke.getPunchline());
        return dto;
    }
}
