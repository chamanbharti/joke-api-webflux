package joke.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import joke.dto.JokeAPIResponseDTO;
import joke.dto.JokeResponseDTO;
import joke.dto.JokeSaveDTO;
import joke.model.Joke;
import joke.model.JokeProperties;
import joke.repository.JokeRepository;

@Service
public class JokeService {

	private final JokeProperties jokeProperties;
	
    @Autowired
    private JokeRepository jokeRepository;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    public JokeService(JokeProperties jokeProperties) {
        this.jokeProperties = jokeProperties;
    }
    public List<JokeResponseDTO> getJokes(int count) {
    	 // Retrieve all existing jokes from the database
        List<Joke> existingJokes = jokeRepository.findAll();
       // Get the count of existing jokes 
        int existingCount = existingJokes.size();
        // Calculate how many more jokes are needed to meet the requested count
        int remainingJokesNeeded = count - existingCount;

        // Check if additional jokes are needed
        if (remainingJokesNeeded > 0) {
        	 // Fetch new jokes in batches, as needed
            List<JokeSaveDTO> newJokes = fetchJokesInBatches(remainingJokesNeeded);
            // Filter out any duplicates from the newly fetched jokes
            List<JokeSaveDTO> uniqueNewJokes = filterUniqueJokes(newJokes, existingJokes);
            // Save the unique new jokes to the database
            jokeRepository.saveAll(uniqueNewJokes.stream()
                                           .map(this::toEntity)// Convert DTOs to entities
                                           .collect(Collectors.toList()));
         // Add the unique new jokes to the existing jokes list
            existingJokes.addAll(uniqueNewJokes.stream()
                                 .map(this::toEntity)// Convert DTOs to entities
                                 .collect(Collectors.toList()));
        }

        // Convert the existing jokes to JokeResponseDTOs and limit the result to the requested count
        return existingJokes.stream()
                    .limit(count)// Limit the number of jokes returned to the requested count
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
    }

    /**
     * Filters out duplicates from the new jokes based on existing jokes in the database.
     *
     * @param newJokes       The list of new jokes to be checked for uniqueness.
     * @param existingJokes  The list of existing jokes in the database.
     * @return A list of unique new jokes.
     */
    private List<JokeSaveDTO> filterUniqueJokes(List<JokeSaveDTO> newJokes, List<Joke> existingJokes) {
        Set<String> existingJokeSet = existingJokes.stream()
            .map(Joke::getSetup)  // Assuming the joke's setup (question) is unique
            .collect(Collectors.toSet());

        return newJokes.stream()
            .filter(newJoke -> !existingJokeSet.contains(newJoke.getSetup()))
            .collect(Collectors.toList());
    }

    /**
     * Fetches jokes in batches, with each batch containing 10 jokes.
     * 
     * @param count The total number of jokes to fetch.
     * @return A list of JokeSaveDTOs fetched from the external API.
     */
    public List<JokeSaveDTO> fetchJokesInBatches(int count) {
        List<JokeSaveDTO> allNewJokes = new ArrayList<>();
//        int batches = (count + BATCH_SIZE - 1) / BATCH_SIZE;  // Calculate number of batches required
        int batchSize = jokeProperties.getBatchSize();
        if (batchSize == 0) {
            throw new IllegalArgumentException("Batch size cannot be zero");
        }
        int batches = (count + jokeProperties.getBatchSize() - 1) / jokeProperties.getBatchSize();  // Calculate number of batches required

        for (int batch = 0; batch < batches; batch++) {
            int jokesToFetch = Math.min(jokeProperties.getBatchSize(), count - (batch * jokeProperties.getBatchSize()));  // Fetch only required jokes in the last batch
            allNewJokes.addAll(fetchBatch(jokesToFetch));
        }

        return allNewJokes;
    }

    /**
     * Fetches a single batch of jokes from the external API.
     * 
     * @param count The number of jokes to fetch in this batch.
     * @return A list of JokeSaveDTOs fetched in the current batch.
     */
    private List<JokeSaveDTO> fetchBatch(int count) {
        List<JokeSaveDTO> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                //JokeAPIResponseDTO apiJoke = restTemplate.getForObject(JOKE_API_URL, JokeAPIResponseDTO.class);
            	JokeAPIResponseDTO apiJoke = restTemplate.getForObject("https://official-joke-api.appspot.com/random_joke", JokeAPIResponseDTO.class);
                batch.add(toSaveDTO(apiJoke));  // Convert JokeAPIResponseDTO to JokeSaveDTO
            } catch (RestClientException e) {
                // Handle external API error, logging the exception or implementing retry logic
                System.err.println("Error fetching joke from API: " + e.getMessage());
            }
        }
        return batch;
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

    /**
     * Converts a JokeAPIResponseDTO to a JokeSaveDTO.
     */
    private JokeSaveDTO toSaveDTO(JokeAPIResponseDTO apiDTO) {
        JokeSaveDTO dto = new JokeSaveDTO();
        dto.setType(apiDTO.getType());
        dto.setSetup(apiDTO.getSetup());
        dto.setPunchline(apiDTO.getPunchline());
        return dto;
    }
}
