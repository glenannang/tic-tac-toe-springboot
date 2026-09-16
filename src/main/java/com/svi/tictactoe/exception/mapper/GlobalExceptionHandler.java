package com.svi.tictactoe.exception.mapper;

import com.svi.tictactoe.exception.RoomDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomDoesNotExistException.class)
    public ResponseEntity<String> handleRoomDoesNotExist(RoomDoesNotExistException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }




}
