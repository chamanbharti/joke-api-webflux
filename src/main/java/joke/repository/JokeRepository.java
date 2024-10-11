package joke.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import joke.model.Joke;

public interface JokeRepository extends JpaRepository<Joke, Long> {
	boolean existsBySetup(String setup);  // Custom query method to check if a joke exists by setup
}
