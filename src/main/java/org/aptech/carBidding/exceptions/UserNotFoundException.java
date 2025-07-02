package org.aptech.carBidding.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Construct a new UserNotFoundException with a custom message.
     *
     * @param message human-readable error message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct a new UserNotFoundException with a default message.
     */
    public UserNotFoundException() {
        super("User not found");
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("status","404","error","Not Found","message",ex.getMessage()));
    }
}
