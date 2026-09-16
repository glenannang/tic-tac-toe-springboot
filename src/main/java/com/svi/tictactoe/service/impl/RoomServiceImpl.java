package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.request.CreateRoomRequest;
import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.response.RoomResponse;
import com.svi.tictactoe.dto.response.RoomStatusResponse;
import com.svi.tictactoe.entity.Room;
import com.svi.tictactoe.enums.PlayerSymbol;
import com.svi.tictactoe.enums.RoomStatus;
import com.svi.tictactoe.exception.*;
import com.svi.tictactoe.mapper.RoomMapper;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.RoomRepository;
import com.svi.tictactoe.service.RoomService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

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
    public RoomResponse createRoom(String roomCode, CreateRoomRequest request){
        // CHECK IF ROOM ALREADY EXISTS
        if (roomRepository.findById(roomCode).isPresent()) {
            throw new RoomAlreadyExistsException("Room already exists.");
        }

        // IF ROOM DOES NOT EXIST YET

        Room room= new Room();

        room.setRoomCode(roomCode);
        room.setHostPlayerId(request.getPlayerId());
        room.setPlayerCount(1);
        room.setStatus(RoomStatus.WAITING);
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room, request.getPlayerId(),PlayerSymbol.X,"Room created successfully.");

    }


    @Override
    public RoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        // NO ROOM YET
        if (roomRepository.findById(roomCode).isEmpty()) {
            throw new RoomDoesNotExistException("Room does not exist.");
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


            // insert creating a game here later
            room.setStatus(RoomStatus.IN_GAME);
            room.setUpdatedAt(Instant.now());
            roomRepository.save(room);

            return roomMapper.toRoomResponse(room, request.getPlayerId(), PlayerSymbol.O,"Room joined successfully."
            );
        }
        // GAME IN PROGRESS
        else if (room.getStatus() == RoomStatus.IN_GAME) {

            throw new RoomUnavailableException("Cannot join. Room currently has an ongoing game.");

        }
        // REMATCH STATE
        else {
            throw new RoomUnavailableException("Cannot join. Room is currently in the rematch phase.");
        }
    }

    @Override
    public RoomStatusResponse getRoomStatus(String roomCode){
        Optional<Room> roomOptional = roomRepository.findById(roomCode);

        if (roomOptional.isEmpty()) {
            throw new RoomNotFoundException("Room does not exist.");
        }

        Room room = roomOptional.get();

        return  roomMapper.toRoomStatusResponse(room);

    }






}