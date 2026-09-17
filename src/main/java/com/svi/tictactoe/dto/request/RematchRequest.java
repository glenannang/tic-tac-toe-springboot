package com.svi.tictactoe.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RematchRequest {

    @NotNull(message = "Player ID is required.")
    private UUID playerId;

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
