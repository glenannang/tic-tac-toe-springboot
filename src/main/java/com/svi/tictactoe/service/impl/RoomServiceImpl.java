package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.response.JoinRoomResponse;
import com.svi.tictactoe.entity.Room;
import com.svi.tictactoe.enums.PlayerMark;
import com.svi.tictactoe.enums.RoomStatus;
import com.svi.tictactoe.exception.PlayerAlreadyInRoomException;
import com.svi.tictactoe.exception.RoomFullException;
import com.svi.tictactoe.mapper.RoomMapper;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.RoomRepository;
import com.svi.tictactoe.service.RoomService;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository, GameRepository gameRepository,RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.gameRepository = gameRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    public JoinRoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        // NO ROOM YET
        if (roomRepository.findById(roomCode).isEmpty()) {
            Room room = new Room();

            room.setRoomCode(roomCode);
            room.setHostPlayerId(request.getPlayerId());
            room.setPlayerCount(1);
            room.setStatus(RoomStatus.WAITING);
            room.setCreatedAt(Instant.now());
            room.setUpdatedAt(Instant.now());

            roomRepository.save(room);

            return roomMapper.toJoinRoomResponse(room, request.getPlayerId(), PlayerMark.X);
        }

        //ROOM ALREADY EXISTS
        Room room = roomRepository.findById(roomCode).get();

        // CHECK IF SAME PLAYER IS TRYING TO JOIN
        if (room.getHostPlayerId().equals(request.getPlayerId())) {
            throw new PlayerAlreadyInRoomException("Player is already in the room.");
        }

        // WAITING STATE
        if (room.getStatus() == RoomStatus.WAITING) {

            room.setGuestPlayerId(request.getPlayerId());
            room.setPlayerCount(2);
            room.setStatus(RoomStatus.FULL);
            room.setUpdatedAt(Instant.now());

            // insert creating a game here later

            roomRepository.save(room);

            return roomMapper.toJoinRoomResponse(room, request.getPlayerId(), PlayerMark.O
            );
        }

        // FULL STATE
        throw new RoomFullException("Room is already full.");
    }
}