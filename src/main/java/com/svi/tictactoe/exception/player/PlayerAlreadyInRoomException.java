package com.svi.tictactoe.exception.player;

public class PlayerAlreadyInRoomException extends RuntimeException {

    public PlayerAlreadyInRoomException(String message) {
        super(message);
    }
}