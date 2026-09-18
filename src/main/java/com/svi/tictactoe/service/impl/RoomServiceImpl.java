package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.constant.SuccessMessages;
import com.svi.tictactoe.dto.request.CreateRoomRequest;
import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.request.LeaveRoomRequest;
import com.svi.tictactoe.dto.request.RematchRequest;
import com.svi.tictactoe.dto.response.LeaveRoomResponse;
import com.svi.tictactoe.dto.response.RematchResponse;
import com.svi.tictactoe.dto.response.RoomResponse;
import com.svi.tictactoe.dto.response.RoomStatusResponse;
import com.svi.tictactoe.entity.*;
import com.svi.tictactoe.enums.*;
import com.svi.tictactoe.exception.*;
import com.svi.tictactoe.mapper.RoomMapper;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.PlayerGameRepository;
import com.svi.tictactoe.repository.RoomRepository;
import com.svi.tictactoe.service.GameService;
import com.svi.tictactoe.service.PlayerService;
import com.svi.tictactoe.service.RoomService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final GameService gameService;
    private final PlayerService playerService;


    public RoomServiceImpl(RoomRepository roomRepository,
                           GameService gameService,
                           PlayerService playerService,
                           RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.gameService= gameService;
        this.playerService=playerService;
    }

    @Override
    public RoomResponse createRoom(String roomCode, CreateRoomRequest request){

        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        // CHECK IF ROOM ALREADY EXISTS
        if (roomRepository.findFirstByKeyRoomCode(roomCode).isPresent()) {
            throw new RoomAlreadyExistsException(ErrorMessages.ROOM_ALREADY_EXISTS.getMessage());
        }

        // IF ROOM DOES NOT EXIST YET
        Instant now = Instant.now();

        RoomKey roomKey = new RoomKey();
        roomKey.setRoomCode(roomCode);
        roomKey.setCreatedAt(now);

        Room room = new Room();
        room.setKey(roomKey);

        room.setHostPlayerId(request.getPlayerId());
        room.setStatus(RoomStatus.WAITING);

        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);

        return roomMapper.toRoomResponse(room, request.getPlayerId(),PlayerSymbol.X,SuccessMessages.ROOM_CREATED_SUCCESSFULLY.getMessage());

    }


    @Override
    public RoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        // check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        //get latest row with the given roomCode
        Room room = roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        UUID playerId = request.getPlayerId();

        // closed room cannot be joined
        if (room.getStatus() == RoomStatus.CLOSED) {
            throw new RoomUnavailableException(ErrorMessages.CANNOT_JOIN_CLOSED_ROOM.getMessage());
        }

        // prevents the same player to join
        if (playerId.equals(room.getHostPlayerId()) || playerId.equals(room.getGuestPlayerId())) {
            throw new PlayerAlreadyInRoomException(ErrorMessages.PLAYER_ALREADY_IN_ROOM.getMessage());
        }

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new RoomUnavailableException(ErrorMessages.CANNOT_JOIN_FULL_ROOM.getMessage());
        }

        room.setGuestPlayerId(request.getPlayerId());
        room.setStatus(RoomStatus.READY);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        return roomMapper.toRoomResponse(room, playerId, PlayerSymbol.O,SuccessMessages.ROOM_JOINED_SUCCESSFULLY.getMessage());

    }

    @Override
    public RoomStatusResponse getRoomStatus(String roomCode){
        Optional<Room> roomOptional = roomRepository.findFirstByKeyRoomCode(roomCode);

        if (roomOptional.isEmpty()) {
            throw new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage());
        }

        Room room = roomOptional.get();
        return  roomMapper.toRoomStatusResponse(room);

    }

    @Override
    public LeaveRoomResponse leaveRoom(String roomCode, LeaveRoomRequest request) {
        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        Room room = roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        UUID playerId = request.getPlayerId();

        boolean isHost = playerId.equals(room.getHostPlayerId());
        boolean isGuest = playerId.equals(room.getGuestPlayerId());

        if (!isHost && !isGuest) {
            throw new PlayerNotInRoomException(ErrorMessages.PLAYER_NOT_IN_ROOM.getMessage());
        }

        if (room.getStatus() == RoomStatus.CLOSED) {
            return roomMapper.toLeaveRoomResponse(SuccessMessages.ROOM_ALREADY_CLOSED.getMessage());
        }

        //update game status
        if (room.getStatus() == RoomStatus.IN_GAME) {
            gameService.abandonGame(room.getGameId());
        }

        //update room status
        room.setStatus(RoomStatus.CLOSED);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        return roomMapper.toLeaveRoomResponse(SuccessMessages.ROOM_LEFT_SUCCESSFULLY.getMessage());
    }

    @Override
    public RematchResponse rematch(String roomCode, RematchRequest request) {
        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        Room room = roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() -> new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        if (room.getStatus() != RoomStatus.REMATCH) {throw new RoomUnavailableException(ErrorMessages.ROOM_NOT_AVAILABLE_FOR_REMATCH.getMessage());}

        UUID playerId = request.getPlayerId();

        boolean isHost = playerId.equals(room.getHostPlayerId());
        boolean isGuest = playerId.equals(room.getGuestPlayerId());

        if (!isHost && !isGuest) {throw new PlayerNotInRoomException(ErrorMessages.PLAYER_NOT_IN_ROOM.getMessage());}

        if (isHost) {
            room.setHostRematch(true);
        } else {
            room.setGuestRematch(true);
        }

        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        // Only one player has accepted so far
        if (!(room.isHostRematch() && room.isGuestRematch())) {
            return roomMapper.toRematchResponse(SuccessMessages.WAITING_FOR_OTHER_PLAYER_TO_ACCEPT_REMATCH.getMessage(), RoomStatus.REMATCH, null);
        }

        // both players already accepted rematch
        room.setStatus(RoomStatus.FINISHED);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        // create a new game
        Game newGame = gameService.createGameRecord(roomCode, room.getHostPlayerId(), room.getGuestPlayerId());

        //add new room object
        RoomKey newRoomKey = new RoomKey();
        newRoomKey.setRoomCode(roomCode);
        newRoomKey.setCreatedAt(Instant.now());

        Room newRoom = new Room();

        newRoom.setKey(newRoomKey);
        newRoom.setHostPlayerId(room.getHostPlayerId());
        newRoom.setGuestPlayerId(room.getGuestPlayerId());
        newRoom.setGameId(newGame.getGameId());
        newRoom.setStatus(RoomStatus.IN_GAME);
        newRoom.setHostRematch(false);
        newRoom.setGuestRematch(false);
        newRoom.setUpdatedAt(Instant.now());

        roomRepository.save(newRoom);
        return roomMapper.toRematchResponse(SuccessMessages.REMATCH_STARTED.getMessage(), RoomStatus.IN_GAME, newGame.getGameId());
    }
}