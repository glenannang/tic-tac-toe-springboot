package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.entity.Game;
import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.entity.MoveKey;
import com.svi.tictactoe.enums.GameStatus;
import com.svi.tictactoe.exception.*;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.repository.MoveRepository;
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

    public GameServiceImpl(GameRepository gameRepository, MoveRepository moveRepository) {

        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
    }

    //generates a UUID and saves to the database
    @Override
    public UUID createGame(String roomCode, UUID playerXId, UUID playerOId){

        UUID gameId = UUID.randomUUID();
        Game game = new Game();

        game.setGameId(gameId);
        game.setRoomCode(roomCode);
        game.setPlayerXId(playerXId);
        game.setPlayerOId(playerOId);
        game.setCreatedAt(Instant.now());

        gameRepository.save(game);

        return gameId;
    }

    public AddMoveResponse addMove(UUID gameId, AddMoveRequest request){
        //CHECK IF GAME EXISTS
        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isEmpty()) {
            throw new GameDoesNotExistException("Game does not exist.");
        }

        Game game = gameOptional.get();
        List <Move> existingMoves = moveRepository.findByKeyGameId(gameId);


        validatePlayer(game, gameId); //CHECK IF PLAYER BELONGS TO THE GAME
        validateGameInProgress(game); //Check if game is still on going
        validatePositionAvailable(request.getPosition(), existingMoves);
        validateTurn(game, request.getPlayerId(), existingMoves);

        return null;

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
            expectedPlayerId = game.getPlayerOId();
        } else {
            expectedPlayerId = game.getPlayerXId();
        }

        if (!playerId.equals(expectedPlayerId)) {
            throw new InvalidTurnException("It is not this player's turn.");
        }
    }


}
