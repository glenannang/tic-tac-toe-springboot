package com.svi.tictactoe.exception.game;

public class GameDoesNotExistException extends RuntimeException {
    public GameDoesNotExistException(String message) {
        super(message);
    }
}
