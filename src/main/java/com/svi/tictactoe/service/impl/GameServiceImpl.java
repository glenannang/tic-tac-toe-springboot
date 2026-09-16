package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.entity.Game;
import com.svi.tictactoe.repository.GameRepository;
import com.svi.tictactoe.service.GameService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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


}
