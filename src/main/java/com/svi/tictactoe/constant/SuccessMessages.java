package com.svi.tictactoe.constant;

public enum SuccessMessages {

    PLAYER_CREATED_SUCCESSFULLY("Player created successfully."),
    ROOM_CREATED_SUCCESSFULLY("Room created successfully."),
    ROOM_JOINED_SUCCESSFULLY("Room joined successfully."),
    ROOM_ALREADY_CLOSED("Room is already closed."),
    ROOM_LEFT_SUCCESSFULLY("Room left successfully."),
    WAITING_FOR_OTHER_PLAYER_TO_ACCEPT_REMATCH("Waiting for the other player to accept the rematch."),
    REMATCH_STARTED("Rematch started."),
    GAME_CREATED_SUCCESSFULLY("Game created successfully."),
    MOVE_SAVED_SUCCESSFULLY("Move saved successfully."),
    GAME_REMOVED_SUCCESSFULLY("Game removed successfully.");

    private final String message;

    SuccessMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}