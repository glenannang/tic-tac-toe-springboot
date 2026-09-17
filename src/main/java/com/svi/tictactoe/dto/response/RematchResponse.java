package com.svi.tictactoe.dto.response;

import com.svi.tictactoe.enums.RoomStatus;

import java.util.UUID;

public class RematchResponse {

    private String message;
    private RoomStatus status;
    private UUID gameId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}
