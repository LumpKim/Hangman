package kim.jaehoon.hangman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Token error occurred.")
public class TokenError extends RuntimeException {

    public TokenError(String message) {
        super(message);
    }

    public TokenError(String message, Throwable cause) {
        super(message, cause);
    }

}
