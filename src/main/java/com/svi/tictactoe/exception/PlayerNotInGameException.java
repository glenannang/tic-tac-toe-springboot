package com.svi.tictactoe.exception;

public class PlayerNotInGameException extends RuntimeException {
    public PlayerNotInGameException(String message) {
        super(message);
    }
}
