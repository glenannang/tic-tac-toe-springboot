package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.RoomResponse;
import com.svi.tictactoe.dto.response.RoomStatusResponse;
import com.svi.tictactoe.entity.Room;
import com.svi.tictactoe.enums.PlayerSymbol;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RoomMapper {

    public RoomResponse toRoomResponse(Room room, UUID playerId, PlayerSymbol mark, String message) {

        RoomResponse response = new RoomResponse();

        response.setRoomCode(room.getKey().getRoomCode());
        response.setPlayerId(playerId);
        response.setSymbol(mark);
        response.setRoomStatus(room.getStatus());
        response.setGameId(room.getGameId());
        response.setMessage(message);

        return response;
    }

    public RoomStatusResponse toRoomStatusResponse(Room room){
        RoomStatusResponse response = new RoomStatusResponse();
        response.setRoomCode(room.getKey().getRoomCode());
        response.setStatus(room.getStatus());
        response.setLatestGameId(room.getGameId());
        return response;
    }
}