package kim.jaehoon.hangman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The admin is already have a room")
public class AdminConflictException extends RuntimeException {
}
