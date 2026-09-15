package com.svi.tictactoe.dto.response;

import com.svi.tictactoe.enums.RoomStatus;

import java.util.UUID;

public class RoomStatusResponse {
    private String roomCode;
    private RoomStatus status;
    private int playerCount;
    private UUID latestGameId;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public UUID getLatestGameId() {
        return latestGameId;
    }

    public void setLatestGameId(UUID latestGameId) {
        this.latestGameId = latestGameId;
    }
}
