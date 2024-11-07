package by.kostrikov.experience.exception;

import by.kostrikov.experience.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String EUROPE_MINSK = "Europe/Minsk";

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<ErrorDto> handleExecutionException(ExecutionException ex) {
        ErrorDto errorDto = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error occurred while processing the request: " + ex.getMessage(),
                ZonedDateTime.now(ZoneId.of(EUROPE_MINSK))
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ErrorDto> handleInterruptedException(InterruptedException ex) {
        ErrorDto errorDto = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Request processing was interrupted: " + ex.getMessage(),
                ZonedDateTime.now(ZoneId.of(EUROPE_MINSK))
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException ex) {
        ErrorDto errorDto = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected server error: " + ex.getMessage(),
                ZonedDateTime.now(ZoneId.of(EUROPE_MINSK))
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
