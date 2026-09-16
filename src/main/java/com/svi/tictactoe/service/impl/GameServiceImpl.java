package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.request.CreateGameRequest;
import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.dto.response.CreateGameResponse;
import com.svi.tictactoe.engine.GameEngine;
import com.svi.tictactoe.entity.Game;
import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.entity.MoveKey;
import com.svi.tictactoe.entity.Room;

import com.svi.tictactoe.enums.GameResult;
import com.svi.tictactoe.enums.GameStatus;
import com.svi.tictactoe.enums.PlayerSymbol;
import com.svi.tictactoe.enums.RoomStatus;
import com.svi.tictactoe.exception.*;
import com.svi.tictactoe.mapper.GameMapper;
import com.svi.tictactoe.mapper.MoveMapper;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.MoveRepository;
import com.svi.tictactoe.repository.RoomRepository;

import com.svi.tictactoe.service.GameService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;
    private final RoomRepository roomRepository;
    private final GameMapper gameMapper;
    private final MoveMapper moveMapper;
    private final GameEngine gameEngine;

    public GameServiceImpl(GameRepository gameRepository,
                           MoveRepository moveRepository,
                           RoomRepository roomRepository,
                           GameMapper gameMapper,
                           MoveMapper moveMapper,
                           GameEngine gameEngine) {

        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.moveRepository = moveRepository;
        this.gameMapper = gameMapper;
        this.moveMapper = moveMapper;
        this.gameEngine = gameEngine;
    }


    //generates a UUID and saves to the database
    @Override
    public CreateGameResponse createGame(CreateGameRequest request){
        //check if room exists
        Room room = roomRepository.findFirstByKeyRoomCode(request.getRoomCode()).orElseThrow(() ->
                        new RoomDoesNotExistException("Room does not exist."));

        if (room.getStatus() != RoomStatus.READY) {
            throw new RoomUnavailableException("Game can only be created when the room is ready with two players.");
        }


        UUID gameId = UUID.randomUUID();
        Game game = new Game();

        game.setGameId(gameId);
        game.setRoomCode(request.getRoomCode());
        game.setPlayerXId(room.getHostPlayerId());
        game.setPlayerOId(room.getGuestPlayerId());
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCreatedAt(Instant.now());

        Game savedGame = gameRepository.save(game);

        //update room record
        room.setGameId(gameId);
        room.setStatus(RoomStatus.IN_GAME);
        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);

        return gameMapper.toCreateGameResponse(savedGame, "Game created successfully.");
    }

    public AddMoveResponse addMove(UUID gameId, AddMoveRequest request){

        //check if game exists
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameDoesNotExistException("Game does not exist."));

        List <Move> existingMoves = moveRepository.findByKeyGameId(gameId);

        // Move validations
        validatePlayer(game, request.getPlayerId());                        //Check if player belongs to the game
        validateGameInProgress(game);                                       //Check if game is still on going
        validatePositionAvailable(request.getPosition(), existingMoves);    // Check if position is available
        validateTurn(game, request.getPlayerId(), existingMoves);           // Check if player's turn

        Move move = new Move();


        MoveKey moveKey = new MoveKey();
        moveKey.setGameId(gameId);
        moveKey.setCreatedAt(Instant.now());

        move.setKey(moveKey);
        move.setPlayerId(request.getPlayerId());
        PlayerSymbol symbol = determinePlayerSymbol(game, request.getPlayerId());
        move.setSymbol(symbol);
        move.setPosition(request.getPosition());

        Move savedMove = moveRepository.save(move);
        existingMoves.add(savedMove);
        int moveNumber = existingMoves.size();

        // check if the move cause a win/draw

        if (gameEngine.hasWon(existingMoves, symbol)) {
            game.setStatus(GameStatus.FINISHED);
            game.setResult(GameResult.WIN);
            game.setWinnerId(request.getPlayerId());
            game.setEndedAt(Instant.now());

            gameRepository.save(game);

        } else if (gameEngine.isDraw(existingMoves)) {
            game.setStatus(GameStatus.FINISHED);
            game.setResult(GameResult.DRAW);
            game.setWinnerId(null);
            game.setEndedAt(Instant.now());

            gameRepository.save(game);
        }
        
        return moveMapper.toAddMoveResponse(moveNumber, "Move saved successfully.");

    }

    // HELPER FUNCTIONS FOR VALIDATING A MOVE REQUEST

    //check if player belongs to the game
    private void validatePlayer(Game game, UUID playerId) {
        if (!playerId.equals(game.getPlayerXId()) && !playerId.equals(game.getPlayerOId())) {
            throw new PlayerNotInGameException("Player does not belong to this game.");
        }
    }

    //check if game is still on going
    private void validateGameInProgress(Game game){

        if(game.getStatus() != GameStatus.IN_PROGRESS){
            throw new GameAlreadyFinishedException( "Game is no longer in progress.");
        }

    }

    //check if the position is available
    private void validatePositionAvailable(int position, List<Move> existingMoves) {

        for (Move move : existingMoves) {
            if (move.getPosition() == position) {throw new PositionAlreadyTakenException("Position is already taken.");
            }
        }
    }

    //check if the turn is valid
    private void validateTurn(Game game, UUID playerId, List<Move> existingMoves) {

        // First move is always Player X
        if (existingMoves.isEmpty()) {
            if (!playerId.equals(game.getPlayerXId())) {
                throw new InvalidTurnException("It is Player X's turn.");
            }
            return;
        }

        Move lastMove = existingMoves.getLast();
        UUID expectedPlayerId;

        if (lastMove.getPlayerId().equals(game.getPlayerXId())) {
            expectedPlayerId = game.getPlayerOId();  //  O's turn if last move belongs to player with x symbol
        } else {
            expectedPlayerId = game.getPlayerXId();
        }

        if (!playerId.equals(expectedPlayerId)) {
            throw new InvalidTurnException("It is not this player's turn.");
        }
    }


    private PlayerSymbol determinePlayerSymbol(Game game, UUID playerId) {

        if (playerId.equals(game.getPlayerXId())) {
            return PlayerSymbol.X;
        }

        return PlayerSymbol.O;
    }

}
