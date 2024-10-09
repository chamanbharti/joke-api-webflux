package joke.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import joke.dto.JokeResponseDTO;
import joke.model.Joke;
import joke.repository.JokeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JokeService {

	@Autowired
    private JokeRepository jokeRepository;

    private final WebClient webClient = WebClient.create("https://official-joke-api.appspot.com");

   
    public Flux<JokeResponseDTO> getJokes(int count) {
        return Flux.range(0, count)
                .flatMap(i -> fetchJokeFromApi()) // Fetch jokes asynchronously
                .map(joke -> {
                    JokeResponseDTO jokeResponseDTO = new JokeResponseDTO(
                            UUID.randomUUID().toString(), // generate unique ID
                            joke.getSetup(),
                            joke.getPunchline()
                    );
                    jokeRepository.save(joke); // Saving in a non-reactive way (H2)
                    return jokeResponseDTO;
                });
    }
   
    /*
    public Flux<JokeResponseDTO> getJokes(int count) {
        int batchSize = 10; // Batch size as per current scenario

        return Flux.range(0, count)
                .buffer(batchSize) // Group the range into batches of size 10
                .flatMap(batch -> Flux.fromIterable(batch) // Process each batch
                    .flatMap(i -> fetchJokeFromApi()) // Fetch jokes asynchronously
                    .collectList() // Collect jokes in a list in one batch
                    .flatMapMany(jokes -> {
                        // Save all jokes in a batch reactively
                        return jokeRepository.saveAll(jokes)
                            .thenMany(Flux.fromIterable(jokes)) // Return jokes after saving
                            .map(joke -> new JokeResponseDTO(
                                UUID.randomUUID().toString(),
                                joke.getSetup(),
                                joke.getPunchline()
                            ));
                    })
                );
    }*/

    private Mono<Joke> fetchJokeFromApi() {
        return webClient.get()
                .uri("/random_joke")
                .retrieve()
                .bodyToMono(Joke.class);
    }

    
}

