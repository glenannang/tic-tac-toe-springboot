package com.svi.tictactoe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AddMoveRequest {

    @NotNull(message = "Player ID is required.")
    private UUID playerId;

    @NotNull(message = "Position is required.")
    @Min(value = 0, message = "Position must be between 0 and 8.")
    @Max(value = 8, message = "Position must be between 0 and 8.")
    private int position;


    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
