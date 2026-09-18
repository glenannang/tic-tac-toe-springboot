package com.svi.tictactoe.exception;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.exception.game.GameAlreadyFinishedException;
import com.svi.tictactoe.exception.game.GameDoesNotExistException;
import com.svi.tictactoe.exception.game.InvalidTurnException;
import com.svi.tictactoe.exception.game.PositionAlreadyTakenException;
import com.svi.tictactoe.exception.player.PlayerAlreadyInRoomException;
import com.svi.tictactoe.exception.player.PlayerDoesNotExistException;
import com.svi.tictactoe.exception.player.PlayerNotInGameException;
import com.svi.tictactoe.exception.player.PlayerNotInRoomException;
import com.svi.tictactoe.exception.room.RoomAlreadyExistsException;
import com.svi.tictactoe.exception.room.RoomDoesNotExistException;
import com.svi.tictactoe.exception.room.RoomUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.UUID;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RoomDoesNotExistException.class,
            GameDoesNotExistException.class,
            PlayerDoesNotExistException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }


    @ExceptionHandler({
            GameAlreadyFinishedException.class,
            InvalidTurnException.class,
            PlayerAlreadyInRoomException.class,
            PositionAlreadyTakenException.class,
            RoomAlreadyExistsException.class,
            RoomUnavailableException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({
            PlayerNotInGameException.class,
            PlayerNotInRoomException.class
    })

    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {

        if (exception.getRequiredType() == UUID.class) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(ErrorMessages.INVALID_UUID.getMessage()));
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessages.INVALID_REQUEST_FORMAT.getMessage()));
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException exception) {

        if (exception.getCause() instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType() == UUID.class) {

            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessages.INVALID_UUID.getMessage()));
        }

        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorMessages.INVALID_REQUEST_FORMAT.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ErrorMessages.UNEXPECTED_INTERNAL_SERVER_ERROR.getMessage()));
    }





}
