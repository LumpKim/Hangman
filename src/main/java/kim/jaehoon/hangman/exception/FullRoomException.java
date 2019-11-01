package kim.jaehoon.hangman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Room is full.")
public class FullRoomException extends RuntimeException {
}
