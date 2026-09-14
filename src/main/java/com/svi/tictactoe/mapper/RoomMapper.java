package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.JoinRoomResponse;
import com.svi.tictactoe.entity.Room;
import com.svi.tictactoe.enums.PlayerMark;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RoomMapper {

    public JoinRoomResponse toJoinRoomResponse(Room room, UUID playerId, PlayerMark mark) {

        JoinRoomResponse response = new JoinRoomResponse();

        response.setRoomCode(room.getRoomCode());
        response.setPlayerId(playerId);
        response.setMark(mark);
        response.setRoomStatus(room.getStatus());
        response.setGameId(room.getLatestGameId());

        return response;
    }
}