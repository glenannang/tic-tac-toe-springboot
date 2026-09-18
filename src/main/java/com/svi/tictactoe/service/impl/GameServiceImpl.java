package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.constant.SuccessMessages;
import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.request.CreateGameRequest;
import com.svi.tictactoe.dto.response.*;
import com.svi.tictactoe.engine.GameEngine;
import com.svi.tictactoe.entity.*;

import com.svi.tictactoe.enums.*;
import com.svi.tictactoe.exception.*;
import com.svi.tictactoe.mapper.GameMapper;
import com.svi.tictactoe.mapper.MoveMapper;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.MoveRepository;
import com.svi.tictactoe.repository.PlayerGameRepository;
import com.svi.tictactoe.repository.RoomRepository;

import com.svi.tictactoe.service.GameService;
import com.svi.tictactoe.service.PlayerService;
import com.svi.tictactoe.util.BoardUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;
    private final RoomRepository roomRepository;
    private final PlayerGameRepository playerGameRepository;
    private final GameMapper gameMapper;
    private final MoveMapper moveMapper;
    private final GameEngine gameEngine;
    private final PlayerService playerService;

    public GameServiceImpl(GameRepository gameRepository,
                           MoveRepository moveRepository,
                           RoomRepository roomRepository,
                           PlayerGameRepository playerGameRepository,
                           PlayerService playerService,
                           GameMapper gameMapper,
                           MoveMapper moveMapper,
                           GameEngine gameEngine) {

        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.moveRepository = moveRepository;
        this.playerGameRepository = playerGameRepository;
        this.gameMapper = gameMapper;
        this.moveMapper = moveMapper;
        this.gameEngine = gameEngine;
        this.playerService = playerService;
    }


    //generates a UUID and saves to the database
    @Override
    public CreateGameResponse createGame(CreateGameRequest request){
        //check if room exists
        Room room = roomRepository.findFirstByKeyRoomCode(request.getRoomCode()).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        if (room.getStatus() == RoomStatus.CLOSED) {throw new RoomUnavailableException(ErrorMessages.CANNOT_CREATE_GAME_ROOM_CLOSED.getMessage());}
        if (room.getStatus() == RoomStatus.IN_GAME) {throw new RoomUnavailableException(ErrorMessages.CANNOT_CREATE_GAME_ROOM_HAS_ONGOING_GAME.getMessage());}
        if (room.getStatus() == RoomStatus.REMATCH) {throw new RoomUnavailableException(ErrorMessages.CANNOT_CREATE_GAME_ROOM_WAITING_FOR_REMATCH.getMessage());}
        if (room.getStatus() == RoomStatus.WAITING) {throw new RoomUnavailableException(ErrorMessages.CANNOT_CREATE_GAME_WAITING_FOR_PLAYER.getMessage());}

        Game savedGame = createGameRecord(request.getRoomCode(), room.getHostPlayerId(), room.getGuestPlayerId());

        //update room record
        room.setGameId(savedGame.getGameId());
        room.setStatus(RoomStatus.IN_GAME);
        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);

        return gameMapper.toCreateGameResponse(savedGame, SuccessMessages.GAME_CREATED_SUCCESSFULLY.getMessage());
    }

    @Override
    public Game createGameRecord(String roomCode, UUID playerXId, UUID playerOId) {

        Game game = new Game();

        game.setGameId(UUID.randomUUID());
        game.setRoomCode(roomCode);
        game.setPlayerXId(playerXId);
        game.setPlayerOId(playerOId);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCreatedAt(Instant.now());

        Game savedGame = gameRepository.save(game);

        savePlayerGames(savedGame);

        return savedGame;
    }

    @Override
    public AddMoveResponse addMove(UUID gameId, AddMoveRequest request){

        playerService.validatePlayerExists(request.getPlayerId());

        //check if game exists
        Game game = getGame(gameId);

        List <Move> existingMoves = moveRepository.findByKeyGameId(gameId);

        // Move validations
        validatePlayer(game, request.getPlayerId());
        validateGameInProgress(game);
        validateTurn(game, request.getPlayerId(), existingMoves);
        validatePositionAvailable(request.getPosition(), existingMoves);


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
            handleWin(game, request.getPlayerId());

        } else if (gameEngine.isDraw(existingMoves)) {
            handleDraw(game);
        }
        
        return moveMapper.toAddMoveResponse(moveNumber, SuccessMessages.MOVE_SAVED_SUCCESSFULLY.getMessage());

    }

    @Override
    public GameStatusResponse getGameStatus(UUID gameId) {

        Game game = getGame(gameId);

        List<Move> moves = moveRepository.findByKeyGameId(gameId);

        //construct the board
        List<PlayerSymbol> board = BoardUtil.buildBoard(moves);

        //determine nextTurn
        PlayerSymbol nextTurn = null;

        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            nextTurn = gameEngine.getNextTurn(moves);
        }

        return gameMapper.toGameStatusResponse(game, board, nextTurn, moves.size()
        );
    }

    @Override
    public void abandonGame(UUID gameId) {

        Game game = getGame(gameId);

        game.setStatus(GameStatus.ABANDONED);
        game.setResult(GameResult.INCOMPLETE);
        game.setEndedAt(Instant.now());

        gameRepository.save(game);

        updatePlayerGameResultsForIncomplete(game);

        playerService.recordIncompleteGame(game.getPlayerXId(), game.getPlayerOId());
        playerService.recordGamePlayed(game.getPlayerXId());
        playerService.recordGamePlayed(game.getPlayerOId());
    }


    @Override
    public RemoveGameResponse removeGame(UUID gameId) {

        Game game = getGame(gameId);

        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new GameAlreadyFinishedException(ErrorMessages.GAME_CANNOT_BE_REMOVED.getMessage());
        }

        abandonGame(gameId);
        updateRoomAfterGameRemoval(game);
        return gameMapper.toRemoveGameResponse(SuccessMessages.GAME_REMOVED_SUCCESSFULLY.getMessage());
    }

    @Override
    public BoardStatusResponse getBoardStatus(UUID gameId) {

        Game game = getGame(gameId);

        List<Move> moves = moveRepository.findByKeyGameId(gameId);
        //build the board
        List<PlayerSymbol> board = BoardUtil.buildBoard(moves);

        return gameMapper.toBoardStatusResponse(board);
    }


    // HELPER FUNCTIONS FOR VALIDATING A MOVE REQUEST

    //check if player belongs to the game
    private void validatePlayer(Game game, UUID playerId) {

        if (!playerId.equals(game.getPlayerXId()) && !playerId.equals(game.getPlayerOId())) {
            throw new PlayerNotInGameException(ErrorMessages.PLAYER_NOT_IN_GAME.getMessage());
        }

    }

    //check if game is still on going
    private void validateGameInProgress(Game game){

        if(game.getStatus() != GameStatus.IN_PROGRESS){
            throw new GameAlreadyFinishedException(ErrorMessages.GAME_NO_LONGER_IN_PROGRESS.getMessage());
        }

    }

    //check if the position is available
    private void validatePositionAvailable(int position, List<Move> existingMoves) {

        for (Move move : existingMoves) {
            if (move.getPosition() == position) {
                throw new PositionAlreadyTakenException(ErrorMessages.POSITION_ALREADY_TAKEN.getMessage());
            }
        }

    }

    //check if the turn is valid
    private void validateTurn(Game game, UUID playerId, List<Move> existingMoves) {
        // First move is always Player X
        if (existingMoves.isEmpty()) {

            if (!playerId.equals(game.getPlayerXId())) {
                throw new InvalidTurnException(ErrorMessages.PLAYER_X_TURN.getMessage());
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

            throw new InvalidTurnException(ErrorMessages.NOT_THIS_PLAYERS_TURN.getMessage());

        }
    }


    // HELPER FUNCTIONS FOR UPDATING RECORDS

    private Game getGame(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow(() ->
                        new GameDoesNotExistException(ErrorMessages.GAME_DOES_NOT_EXIST.getMessage()));
    }

    private PlayerSymbol determinePlayerSymbol(Game game, UUID playerId) {

        if (playerId.equals(game.getPlayerXId())) {
            return PlayerSymbol.X;
        }

        return PlayerSymbol.O;
    }

    private void savePlayerGames(Game game) {

        PlayerGameKey playerXKey = new PlayerGameKey();
        playerXKey.setPlayerId(game.getPlayerXId());
        playerXKey.setGameId(game.getGameId());

        PlayerGame playerXGame = new PlayerGame();
        playerXGame.setKey(playerXKey);

        PlayerGameKey playerOKey = new PlayerGameKey();
        playerOKey.setPlayerId(game.getPlayerOId());
        playerOKey.setGameId(game.getGameId());

        PlayerGame playerOGame = new PlayerGame();
        playerOGame.setKey(playerOKey);

        playerGameRepository.save(playerXGame);
        playerGameRepository.save(playerOGame);
    }

    private PlayerGame getPlayerGame(UUID playerId, UUID gameId) {

        PlayerGameKey key = new PlayerGameKey();
        key.setPlayerId(playerId);
        key.setGameId(gameId);

        return playerGameRepository.findById(key).orElseThrow();
    }


    private void updatePlayerGameResults(Game game, UUID winnerId) {

        PlayerGameKey winnerKey = new PlayerGameKey();
        winnerKey.setPlayerId(winnerId);
        winnerKey.setGameId(game.getGameId());

        PlayerGame winnerGame = playerGameRepository.findById(winnerKey).orElseThrow();

        UUID loserId = winnerId.equals(game.getPlayerXId()) ? game.getPlayerOId() : game.getPlayerXId();

        PlayerGameKey loserKey = new PlayerGameKey();
        loserKey.setPlayerId(loserId);
        loserKey.setGameId(game.getGameId());

        PlayerGame loserGame = playerGameRepository.findById(loserKey).orElseThrow();

        winnerGame.setResult(PlayerGameResult.WIN.name());
        loserGame.setResult(PlayerGameResult.LOSS.name());

        playerGameRepository.save(winnerGame);
        playerGameRepository.save(loserGame);
    }

    private void updatePlayerGameResultsForDraw(Game game) {

        PlayerGame playerXGame = getPlayerGame(game.getPlayerXId(), game.getGameId());
        PlayerGame playerOGame = getPlayerGame(game.getPlayerOId(), game.getGameId());

        playerXGame.setResult(PlayerGameResult.DRAW.name());
        playerOGame.setResult(PlayerGameResult.DRAW.name());

        playerGameRepository.save(playerXGame);
        playerGameRepository.save(playerOGame);
    }

    private void updatePlayerGameResultsForIncomplete(Game game) {

        PlayerGame playerXGame = getPlayerGame(game.getPlayerXId(), game.getGameId());
        PlayerGame playerOGame = getPlayerGame(game.getPlayerOId(), game.getGameId());

        playerXGame.setResult(PlayerGameResult.INCOMPLETE.name());
        playerOGame.setResult(PlayerGameResult.INCOMPLETE.name());

        playerGameRepository.save(playerXGame);
        playerGameRepository.save(playerOGame);
    }

    private void updateRoomForRematch(Game game) {

        Room room = roomRepository.findFirstByKeyRoomCode(game.getRoomCode()).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        room.setStatus(RoomStatus.REMATCH);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);
    }

    private void updateRoomAfterGameRemoval(Game game) {

        Room room = roomRepository.findFirstByKeyRoomCode(game.getRoomCode()).orElseThrow(() ->
                        new RoomDoesNotExistException(ErrorMessages.ROOM_DOES_NOT_EXIST.getMessage()));

        room.setStatus(RoomStatus.READY);
        room.setGameId(null);
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);
    }

    private void handleWin(Game game, UUID winnerId) {

        game.setStatus(GameStatus.FINISHED);
        game.setResult(GameResult.WIN);
        game.setWinnerId(winnerId);
        game.setEndedAt(Instant.now());

        gameRepository.save(game);
        updateRoomForRematch(game);
        updatePlayerGameResults(game, winnerId);

        UUID loserId = winnerId.equals(game.getPlayerXId())
                ? game.getPlayerOId()
                : game.getPlayerXId();

        playerService.recordWinAndLoss(winnerId, loserId);
        playerService.recordGamePlayed(winnerId);
        playerService.recordGamePlayed(loserId);

    }

    private void handleDraw(Game game) {

        game.setStatus(GameStatus.FINISHED);
        game.setResult(GameResult.DRAW);
        game.setWinnerId(null);
        game.setEndedAt(Instant.now());

        gameRepository.save(game);
        updateRoomForRematch(game);
        updatePlayerGameResultsForDraw(game);

        playerService.recordDraw(game.getPlayerXId(), game.getPlayerOId());

        playerService.recordGamePlayed(game.getPlayerXId());
        playerService.recordGamePlayed(game.getPlayerOId());

    }

}
