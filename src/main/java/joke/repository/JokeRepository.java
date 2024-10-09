package joke.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import joke.model.Joke;

public interface JokeRepository extends ReactiveCrudRepository<Joke, UUID> {
}
