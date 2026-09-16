package com.svi.tictactoe.dto.response;

import java.util.UUID;

public class CreateGameResponse {

    private String message;
    private UUID gameId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}