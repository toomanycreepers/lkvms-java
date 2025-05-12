package lamart.lkvms.core.utilities.exceptions;

public class ApiRateLimitException extends RuntimeException {
    public ApiRateLimitException() {
        super("API rate limit exceeded.");
    }
}
