package joke.exception;

public class ExternalApiException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExternalApiException(String message) {
        super(message);
    }
}

