package com.svi.tictactoe.constant;

public enum ErrorMessages {

    PLAYER_NOT_FOUND("Player does not exist."),
    PLAYER_ALREADY_IN_ROOM("Player is already in the room."),
    PLAYER_NOT_IN_ROOM("Player does not belong to this room."),
    PLAYER_NOT_IN_GAME("Player does not belong to this game."),
    ROOM_DOES_NOT_EXIST("Room does not exist."),
    ROOM_ALREADY_EXISTS("Room already exists."),
    CANNOT_CREATE_GAME_ROOM_CLOSED("Cannot create game. Room is already closed."),
    CANNOT_CREATE_GAME_ROOM_HAS_ONGOING_GAME("Cannot create game. Room already has an ongoing game."),
    CANNOT_CREATE_GAME_ROOM_WAITING_FOR_REMATCH("Cannot create game. Room is waiting for a rematch."),
    CANNOT_CREATE_GAME_WAITING_FOR_PLAYER("Cannot create game. Waiting for another player to join."),
    CANNOT_JOIN_CLOSED_ROOM("Cannot join. Room is already closed."),
    CANNOT_JOIN_FULL_ROOM("Cannot join. Room is already full."),
    ROOM_NOT_AVAILABLE_FOR_REMATCH("Room is not available for a rematch."),
    GAME_DOES_NOT_EXIST("Game does not exist."),
    GAME_CANNOT_BE_REMOVED("Game can no longer be removed."),
    GAME_NO_LONGER_IN_PROGRESS("Game is no longer in progress."),
    POSITION_ALREADY_TAKEN("Position is already taken."),
    PLAYER_X_TURN("It is Player X's turn."),
    NOT_THIS_PLAYERS_TURN("It is not this player's turn."),
    UNEXPECTED_INTERNAL_SERVER_ERROR("An unexpected internal server error occurred."),
    INVALID_UUID("ID must be a valid UUID."),
    INVALID_REQUEST_FORMAT("Invalid request format.");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}