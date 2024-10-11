package joke.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import joke.dto.JokeAPIResponseDTO;
import joke.dto.JokeSaveDTO;
import joke.exception.ExternalApiException;
import joke.exception.InvalidJokeResponseException;
import joke.model.JokeProperties;

@Service
public class JokeFetchingService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JokeProperties jokeProperties;

    /**
     * Fetches jokes in batches from the external API.
     *
     */
    public List<JokeSaveDTO> fetchJokesInBatches(int count) {
        List<JokeSaveDTO> allNewJokes = new ArrayList<>();
        int batchSize = jokeProperties.getBatchSize();
        int batches = (count + batchSize - 1) / batchSize;

        for (int batch = 0; batch < batches; batch++) {
            int jokesToFetch = Math.min(batchSize, count - (batch * batchSize));
            allNewJokes.addAll(fetchBatch(jokesToFetch));
        }

        return allNewJokes;
    }

    /**
     * Fetches a single batch of jokes from the joke server API.
     */
    /*
    private List<JokeSaveDTO> fetchBatch(int count) {
        List<JokeSaveDTO> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                //JokeAPIResponseDTO apiJoke = restTemplate.getForObject("https://official-joke-api.appspot.com/random_joke", JokeAPIResponseDTO.class);
            	JokeAPIResponseDTO apiJoke = restTemplate.getForObject(jokeProperties.getApiUrl(), JokeAPIResponseDTO.class);
            	batch.add(toSaveDTO(apiJoke));
            } catch (RestClientException e) {
                System.err.println("Error fetching joke from  joke API server: " + e.getMessage());
            }
        }
        return batch;
    }
    */
    private List<JokeSaveDTO> fetchBatch(int count) {
        List<JokeSaveDTO> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int attempts = 0;
            boolean success = false;

            while (!success && attempts < 5) { // Retry up to 5 times
                try {
                    ResponseEntity<JokeAPIResponseDTO> response = restTemplate.exchange(
                            "https://official-joke-api.appspot.com/random_joke",
                            HttpMethod.GET,
                            null,
                            JokeAPIResponseDTO.class
                    );

                    // Validate the response status code
                    if (response.getStatusCode() != HttpStatus.OK) {
                        throw new ExternalApiException("Error fetching joke from API: " + response.getStatusCode());
                    }

                    JokeAPIResponseDTO apiJoke = response.getBody();

                    // Validate the API response
                    if (apiJoke == null || apiJoke.getSetup() == null || apiJoke.getPunchline() == null) {
                        throw new InvalidJokeResponseException("Received invalid joke response from API");
                    }

                    batch.add(toSaveDTO(apiJoke));
                    success = true; // If we reach here, the request was successful

                } catch (HttpClientErrorException e) {
                    // Handle client errors (4xx)
                    System.err.println("Client error fetching joke from API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                    break; // Exit for client errors

                } catch (RestClientException e) {
                    if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        attempts++;
                        System.err.println("Error fetching joke from API: " + e.getMessage());
                        try {
                            // Wait before retrying (exponential backoff)
                            Thread.sleep((long) Math.pow(2, attempts) * 1000); // Waits for 2^attempts seconds
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // Restore the interrupted status
                            break; // Exit if interrupted
                        }
                    } else {
                        System.err.println("Error fetching joke from API: " + e.getMessage());
                        break; // For other exceptions, break out of the loop
                    }
                }
            }
        }
        return batch;
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
