package com.svi.tictactoe.dto.response;


import com.svi.tictactoe.enums.PlayerSymbol;
import com.svi.tictactoe.enums.RoomStatus;

import java.util.UUID;

public class RoomResponse {

    private String roomCode;
    private UUID playerId;
    private PlayerSymbol symbol;
    private RoomStatus roomStatus;
    private UUID gameId;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public PlayerSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(PlayerSymbol mark) {
        this.symbol = mark;
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