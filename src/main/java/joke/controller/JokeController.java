package joke.controller;

import java.security.InvalidParameterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import joke.dto.JokeResponseDTO;
import joke.service.JokeService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/jokes")
public class JokeController {

	@Autowired
    private JokeService jokeService;

    @GetMapping
    public Flux<JokeResponseDTO> getJokes(@RequestParam int count) {
        if (count < 1 || count > 100) {
            throw new InvalidParameterException("Count must be between 1 and 100");
        }

        return jokeService.getJokes(count);
    }
   
}
