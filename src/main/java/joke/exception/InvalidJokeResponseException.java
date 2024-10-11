package joke.exception;

public class InvalidJokeResponseException extends RuntimeException {
    public InvalidJokeResponseException(String message) {
        super(message);
    }
}

