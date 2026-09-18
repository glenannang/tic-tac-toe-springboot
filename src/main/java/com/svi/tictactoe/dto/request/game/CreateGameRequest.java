package com.svi.tictactoe.dto.request.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateGameRequest {

    @NotBlank(message = "Room code is required.")
    @Pattern(
            regexp = "^[0-9a-fA-F]{6}$",
            message = "Room code must be a valid 6-character hexadecimal code."
    )
    private String roomCode;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}