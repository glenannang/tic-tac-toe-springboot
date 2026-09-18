package com.svi.tictactoe.exception.game;

public class PositionAlreadyTakenException extends RuntimeException {
    public PositionAlreadyTakenException(String message) {
        super(message);
    }
}
