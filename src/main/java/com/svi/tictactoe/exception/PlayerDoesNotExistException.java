package com.svi.tictactoe.exception;

public class PlayerDoesNotExistException extends RuntimeException {
    public PlayerDoesNotExistException(String message) {
        super(message);
    }
}
