package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.request.CreateRoomRequest;
import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.request.LeaveRoomRequest;
import com.svi.tictactoe.dto.response.LeaveRoomResponse;
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
    private final PlayerGameRepository playerGameRepository;
    private final GameRepository gameRepository;

    public RoomServiceImpl(RoomRepository roomRepository, GameService gameService,RoomMapper roomMapper, PlayerGameRepository playerGameRepository, GameRepository gameRepository) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.gameService= gameService;
        this.playerGameRepository = playerGameRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public RoomResponse createRoom(String roomCode, CreateRoomRequest request){
        // CHECK IF ROOM ALREADY EXISTS
        if (roomRepository.findFirstByKeyRoomCode(roomCode).isPresent()) {
            throw new RoomAlreadyExistsException("Room already exists.");
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

        return roomMapper.toRoomResponse(room, request.getPlayerId(),PlayerSymbol.X,"Room created successfully.");

    }


    @Override
    public RoomResponse joinRoom(String roomCode, JoinRoomRequest request) {

        //get latest row with the given roomCode
        Room room = roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() ->
                        new RoomDoesNotExistException("Room does not exist."));

        // prevents the same player to join
        if (room.getHostPlayerId().equals(request.getPlayerId())) {
            throw new PlayerAlreadyInRoomException("Player is already in the room.");
        }

        // WAITING STATE
        if (room.getStatus() == RoomStatus.WAITING) {

            room.setGuestPlayerId(request.getPlayerId());
            room.setStatus(RoomStatus.READY);
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
        Optional<Room> roomOptional = roomRepository.findFirstByKeyRoomCode(roomCode);

        if (roomOptional.isEmpty()) {
            throw new RoomDoesNotExistException("Room does not exist.");
        }

        Room room = roomOptional.get();
        return  roomMapper.toRoomStatusResponse(room);

    }

    @Override
    public LeaveRoomResponse leaveRoom(String roomCode, LeaveRoomRequest request) {

        Room room = roomRepository.findFirstByKeyRoomCode(roomCode).orElseThrow(() ->
                        new RoomDoesNotExistException("Room does not exist."));

        UUID playerId = request.getPlayerId();

        boolean isHost = playerId.equals(room.getHostPlayerId());
        boolean isGuest = playerId.equals(room.getGuestPlayerId());

        if (!isHost && !isGuest) {throw new PlayerNotInRoomException("Player does not belong to this room.");}

        if (room.getStatus() == RoomStatus.CLOSED) {return roomMapper.toLeaveRoomResponse("Room is already closed.");}


        //update game status
        if (room.getStatus() == RoomStatus.IN_GAME) {

            Game game = gameRepository.findById(room.getGameId()).orElseThrow(() ->
                            new GameDoesNotExistException("Game does not exist."));

            game.setStatus(GameStatus.ABANDONED);
            game.setResult(GameResult.INCOMPLETE);
            game.setWinnerId(null);
            game.setEndedAt(Instant.now());

            gameRepository.save(game);

            updatePlayerGameResultsForIncomplete(game);
        }

        //update room status
        room.setStatus(RoomStatus.CLOSED);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);


        return roomMapper.toLeaveRoomResponse("Room left successfully.");
    }


    private void updatePlayerGameResultsForIncomplete(Game game) {

        PlayerGameKey playerXKey = new PlayerGameKey();
        playerXKey.setPlayerId(game.getPlayerXId());
        playerXKey.setGameId(game.getGameId());

        PlayerGame playerXGame = playerGameRepository.findById(playerXKey).orElseThrow();

        PlayerGameKey playerOKey = new PlayerGameKey();
        playerOKey.setPlayerId(game.getPlayerOId());
        playerOKey.setGameId(game.getGameId());

        PlayerGame playerOGame = playerGameRepository.findById(playerOKey).orElseThrow();

        playerXGame.setResult(PlayerGameResult.INCOMPLETE.name());
        playerOGame.setResult(PlayerGameResult.INCOMPLETE.name());

        playerGameRepository.save(playerXGame);
        playerGameRepository.save(playerOGame);
    }

}