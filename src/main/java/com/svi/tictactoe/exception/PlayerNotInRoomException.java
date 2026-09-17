package com.svi.tictactoe.exception;

public class PlayerNotInRoomException extends RuntimeException {
    public PlayerNotInRoomException(String message) {
        super(message);
    }
}
