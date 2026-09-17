package com.svi.tictactoe.exception.mapper;

import com.svi.tictactoe.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RoomDoesNotExistException.class,
            GameDoesNotExistException.class,
            PlayerDoesNotExistException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }


    @ExceptionHandler({
            GameAlreadyFinishedException.class,
            InvalidTurnException.class,
            PlayerAlreadyInRoomException.class,
            PositionAlreadyTakenException.class,
            RoomAlreadyExistsException.class,
            RoomUnavailableException.class
    })
    public ResponseEntity<String> handleConflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({
            PlayerNotInGameException.class,
            PlayerNotInRoomException.class
    })

    public ResponseEntity<String> handleForbidden(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }



}
