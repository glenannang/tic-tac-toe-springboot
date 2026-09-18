package com.svi.tictactoe.exception.player;

public class PlayerDoesNotExistException extends RuntimeException {
    public PlayerDoesNotExistException(String message) {
        super(message);
    }
}
