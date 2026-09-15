package com.svi.tictactoe.exception;

public class RoomDoesNotExistException extends RuntimeException {
    public RoomDoesNotExistException(String message) {
        super(message);
    }
}
