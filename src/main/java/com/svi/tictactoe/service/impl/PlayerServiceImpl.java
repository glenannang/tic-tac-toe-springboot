package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.dto.response.CreatePlayerResponse;
import com.svi.tictactoe.entity.Player;
import com.svi.tictactoe.exception.PlayerDoesNotExistException;
import com.svi.tictactoe.mapper.PlayerMapper;
import com.svi.tictactoe.repository.PlayerRepository;
import com.svi.tictactoe.service.PlayerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    @Override
    public CreatePlayerResponse createPlayer() {

        Player player = new Player();

        player.setPlayerId(UUID.randomUUID());
        player.setWins(0);
        player.setLosses(0);
        player.setDraws(0);
        player.setGamesPlayed(0);
        player.setIncompleteGames(0);

        Player savedPlayer = playerRepository.save(player);

        return playerMapper.toCreatePlayerResponse(savedPlayer, "Player created successfully.");
    }

    @Override
    public void validatePlayerExists(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new PlayerDoesNotExistException("Player does not exist.");
        }
    }

}