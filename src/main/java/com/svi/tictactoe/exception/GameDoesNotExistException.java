package com.svi.tictactoe.exception;

public class GameDoesNotExistException extends RuntimeException {
    public GameDoesNotExistException(String message) {
        super(message);
    }
}
