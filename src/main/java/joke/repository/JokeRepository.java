package joke.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import joke.model.Joke;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
