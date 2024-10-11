package joke.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import joke.dto.JokeSaveDTO;
import joke.model.Joke;
import joke.repository.JokeRepository;

@Service
public class JokeSavingService {

    @Autowired
    private JokeRepository jokeRepository;

    /**
     * Filters and saves only the unique jokes to the database.
     */
    public List<Joke> saveUniqueJokes(List<JokeSaveDTO> newJokes, List<Joke> existingJokes) {
        List<JokeSaveDTO> uniqueNewJokes = filterUniqueJokes(newJokes, existingJokes);

        if (!uniqueNewJokes.isEmpty()) {
            List<Joke> jokesToSave = uniqueNewJokes.stream()
                    .map(this::toEntity)
                    .filter(joke -> !jokeRepository.existsBySetup(joke.getSetup()))  // Verify joke existence by setup
                    .collect(Collectors.toList());

            jokeRepository.saveAll(jokesToSave);
            return jokesToSave;
        }
        return new ArrayList<>();
    }

    /**
     * Filters out duplicate jokes based on existing jokes in the database.
     */
    private List<JokeSaveDTO> filterUniqueJokes(List<JokeSaveDTO> newJokes, List<Joke> existingJokes) {
        Set<String> existingJokeSet = existingJokes.stream()
                .map(Joke::getSetup)
                .collect(Collectors.toSet());

        return newJokes.stream()
                .filter(newJoke -> !existingJokeSet.contains(newJoke.getSetup()))
                .collect(Collectors.toList());
    }

    /**
     * Converts a JokeSaveDTO to a Joke entity.
     */
    private Joke toEntity(JokeSaveDTO dto) {
        Joke joke = new Joke();
        joke.setType(dto.getType());
        joke.setSetup(dto.getSetup());
        joke.setPunchline(dto.getPunchline());
        return joke;
    }
}
