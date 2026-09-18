package com.svi.tictactoe.dto.request.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateRoomRequest {

    @NotNull(message = "Player ID is required.")
    private UUID playerId;

    @NotBlank(message = "Room code is required.")
    @Size(min = 6, max = 10, message = "Room code must be between 6 and 10 characters.")
    @Pattern(
            regexp = "^[A-Za-z0-9]+$",
            message = "Room code must contain only letters and numbers."
    )
    private String roomCode;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}