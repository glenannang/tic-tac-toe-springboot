package com.svi.tictactoe.exception.player;

public class PlayerNotInRoomException extends RuntimeException {
    public PlayerNotInRoomException(String message) {
        super(message);
    }
}
