package com.svi.tictactoe.dto.response;

import com.svi.tictactoe.enums.PlayerMark;
import com.svi.tictactoe.enums.RoomStatus;

import java.util.UUID;

public class JoinRoomResponse {

    private String roomCode;
    private UUID playerId;
    private PlayerMark mark;
    private RoomStatus roomStatus;
    private UUID gameId;

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

    public PlayerMark getMark() {
        return mark;
    }

    public void setMark(PlayerMark mark) {
        this.mark = mark;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }
}