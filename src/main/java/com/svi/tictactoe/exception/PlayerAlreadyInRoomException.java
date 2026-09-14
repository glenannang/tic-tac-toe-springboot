package com.svi.tictactoe.exception;

public class PlayerAlreadyInRoomException extends RuntimeException {

    public PlayerAlreadyInRoomException(String message) {
        super(message);
    }
}