package joke.exception;

import java.security.InvalidParameterException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleExternalApiException(RestClientException ex) {
        return new ResponseEntity<>("Failed to fetch joke from https://official-joke-api.appspot.com/random_joke API", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

