package joke;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import joke.controller.JokeController;
import joke.dto.JokeResponseDTO;
import joke.service.JokeService;
import reactor.core.publisher.Flux;

@WebFluxTest(JokeController.class)
public class JokeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JokeService jokeService;

    @Test
    public void testGetJokes() {
        List<JokeResponseDTO> mockJokes = List.of(
                new JokeResponseDTO("1", "What did the ocean say to the shore?", "Nothing, it just waved."),
                new JokeResponseDTO("2", "Why don't skeletons fight each other?", "They don't have the guts.")
        );

        when(jokeService.getJokes(2)).thenReturn(Flux.fromIterable(mockJokes));

        webTestClient.get().uri("/jokes?count=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JokeResponseDTO.class)
                .hasSize(2);
    }
}
