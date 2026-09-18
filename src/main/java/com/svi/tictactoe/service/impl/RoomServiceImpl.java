package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.constant.SuccessMessages;
import com.svi.tictactoe.dto.request.room.CreateRoomRequest;
import com.svi.tictactoe.dto.request.room.JoinRoomRequest;
import com.svi.tictactoe.dto.request.room.LeaveRoomRequest;
import com.svi.tictactoe.dto.request.room.RematchRequest;
import com.svi.tictactoe.dto.response.room.LeaveRoomResponse;
import com.svi.tictactoe.dto.response.room.RematchResponse;
import com.svi.tictactoe.dto.response.room.RoomResponse;
import com.svi.tictactoe.dto.response.room.RoomStatusResponse;
import com.svi.tictactoe.entity.*;
import com.svi.tictactoe.enums.*;
import com.svi.tictactoe.exception.player.PlayerAlreadyInRoomException;
import com.svi.tictactoe.exception.player.PlayerNotInRoomException;
import com.svi.tictactoe.exception.room.RoomAlreadyExistsException;
import com.svi.tictactoe.exception.room.RoomDoesNotExistException;
import com.svi.tictactoe.exception.room.RoomUnavailableException;
import com.svi.tictactoe.mapper.RoomMapper;
import com.svi.tictactoe.repository.RoomRepository;
import com.svi.tictactoe.service.GameService;
import com.svi.tictactoe.service.PlayerService;
import com.svi.tictactoe.service.RoomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

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
    public RoomResponse createRoom(CreateRoomRequest request){

        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        // CHECK IF ROOM ALREADY EXISTS
        if (roomRepository.findFirstByKeyRoomCode(request.getRoomCode()).isPresent()) {
            throw new RoomAlreadyExistsException(ErrorMessages.ROOM_ALREADY_EXISTS.getMessage());
        }

        // IF ROOM DOES NOT EXIST YET
        Instant now = Instant.now();

        RoomKey roomKey = new RoomKey();
        roomKey.setRoomCode(request.getRoomCode());
        roomKey.setCreatedAt(now);

        Room room = new Room();
        room.setKey(roomKey);

        room.setHostPlayerId(request.getPlayerId());
        room.setStatus(RoomStatus.WAITING);

        room.setUpdatedAt(now);

        roomRepository.save(room);

        logger.info("Room created successfully with code {}", request.getRoomCode());
        return roomMapper.toRoomResponse(room, request.getPlayerId(),PlayerSymbol.X, SuccessMessages.ROOM_CREATED_SUCCESSFULLY.getMessage());

    }


    @Override
    public RoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        // check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        //get latest row with the given roomCode
        Room room = getRoom(roomCode);
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

        logger.info("Player joined room {}", roomCode);
        return roomMapper.toRoomResponse(room, playerId, PlayerSymbol.O, SuccessMessages.ROOM_JOINED_SUCCESSFULLY.getMessage());

    }

    @Override
    public RoomStatusResponse getRoomStatus(String roomCode){
        Room room = getRoom(roomCode);
        return  roomMapper.toRoomStatusResponse(room);

    }

    @Override
    public LeaveRoomResponse leaveRoom(String roomCode, LeaveRoomRequest request) {
        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        Room room = getRoom(roomCode);
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

        logger.info("Player left room {}", roomCode);
        return roomMapper.toLeaveRoomResponse(SuccessMessages.ROOM_LEFT_SUCCESSFULLY.getMessage());
    }

    @Override
    public RematchResponse rematch(String roomCode, RematchRequest request) {
        // Check if player exists
        playerService.validatePlayerExists(request.getPlayerId());

        Room room = getRoom(roomCode);

        UUID playerId = request.getPlayerId();

        boolean isHost = playerId.equals(room.getHostPlayerId());
        boolean isGuest = playerId.equals(room.getGuestPlayerId());

        if (!isHost && !isGuest) {
            throw new PlayerNotInRoomException(ErrorMessages.PLAYER_NOT_IN_ROOM.getMessage());}

        if (room.getStatus() != RoomStatus.REMATCH) {
            throw new RoomUnavailableException(ErrorMessages.ROOM_NOT_AVAILABLE_FOR_REMATCH.getMessage());}

        if (isHost) {
            room.setHostRematch(true);
        } else {
            room.setGuestRematch(true);
        }

        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        // Only one player has accepted so far
        if (!(room.isHostRematch() && room.isGuestRematch())) {
            logger.info("Rematch requested for room {}; waiting for the other player", roomCode);
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
        logger.info("Rematch game started for room {} with game {}", roomCode, newGame.getGameId());
        return roomMapper.toRematchResponse(SuccessMessages.REMATCH_STARTED.getMessage(), RoomStatus.IN_GAME, newGame.getGameId());
    }

    // HELPER FUNCTIONS
    private Room getRoom(String roomCode) {

        return roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));
    }

}