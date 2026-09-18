package com.svi.tictactoe.dto.response.room;

import com.svi.tictactoe.enums.RoomStatus;

import java.util.UUID;

public class RoomStatusResponse {
    private String roomCode;
    private RoomStatus status;
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

    public UUID getLatestGameId() {
        return latestGameId;
    }

    public void setLatestGameId(UUID latestGameId) {
        this.latestGameId = latestGameId;
    }
}
