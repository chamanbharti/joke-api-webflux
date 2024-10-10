package joke.controller;

import java.security.InvalidParameterException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import joke.dto.JokeResponseDTO;
import joke.service.JokeService;

@RestController
@RequestMapping("/jokes")
public class JokeController {

	private final JokeService jokeService;

    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }
    
    @GetMapping
    public ResponseEntity<List<JokeResponseDTO>> getJokes(@RequestParam int count) {
        if (count < 1 || count > 100) {
           // return ResponseEntity.badRequest().build();
        	throw new InvalidParameterException("Count must be between 1 and 100");
        }
        List<JokeResponseDTO> jokes = jokeService.getJokes(count);
        return ResponseEntity.ok(jokes);
    }
    
}
