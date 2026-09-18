package com.svi.tictactoe.dto.response.player;

import java.util.UUID;

public class CreatePlayerResponse {

    private UUID playerId;
    private String message;

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
