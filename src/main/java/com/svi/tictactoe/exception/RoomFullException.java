package com.svi.tictactoe.exception;

public class RoomFullException extends RuntimeException {
    public RoomFullException(String message) {
        super(message);
    }
}
