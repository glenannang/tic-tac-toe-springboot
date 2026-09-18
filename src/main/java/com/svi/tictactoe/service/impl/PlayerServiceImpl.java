package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.constant.SuccessMessages;
import com.svi.tictactoe.dto.response.player.CreatePlayerResponse;
import com.svi.tictactoe.entity.Player;
import com.svi.tictactoe.exception.player.PlayerDoesNotExistException;
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

        return playerMapper.toCreatePlayerResponse(savedPlayer, SuccessMessages.PLAYER_CREATED_SUCCESSFULLY.getMessage());
    }

    @Override
    public void validatePlayerExists(UUID playerId) {
        getPlayer(playerId);
    }

    @Override
    public void recordWinAndLoss(UUID winnerId, UUID loserId) {

        Player winner = getPlayer(winnerId);
        Player loser = getPlayer(loserId);

        winner.setWins(winner.getWins() + 1);
        loser.setLosses(loser.getLosses() + 1);

        playerRepository.save(winner);
        playerRepository.save(loser);
    }

    @Override
    public void recordDraw(UUID playerXId, UUID playerOId) {
        Player playerX = getPlayer(playerXId);
        Player playerO = getPlayer(playerOId);

        playerX.setDraws(playerX.getDraws() + 1);
        playerO.setDraws(playerO.getDraws() + 1);

        playerRepository.save(playerX);
        playerRepository.save(playerO);
    }

    @Override
    public void recordGamePlayed(UUID playerId) {
        Player player = getPlayer(playerId);
        player.setGamesPlayed(player.getGamesPlayed() + 1);
        playerRepository.save(player);
    }

    @Override
    public void recordIncompleteGame(UUID playerXId, UUID playerOId) {
        Player playerX = getPlayer(playerXId);
        Player playerO = getPlayer(playerOId);

        playerX.setIncompleteGames(playerX.getIncompleteGames() + 1);
        playerO.setIncompleteGames(playerO.getIncompleteGames() + 1);

        playerRepository.save(playerX);
        playerRepository.save(playerO);
    }

    private Player getPlayer(UUID playerId) {
        return playerRepository.findById(playerId).orElseThrow(() ->
                        new PlayerDoesNotExistException(ErrorMessages.PLAYER_NOT_FOUND.getMessage()));
    }

}