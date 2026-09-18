package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.response.player.CreatePlayerResponse;

import java.util.List;
import java.util.UUID;

public interface PlayerService {

    CreatePlayerResponse createPlayer();
    void validatePlayerExists(UUID playerId);
    void recordWinAndLoss(UUID winnerId, UUID loserId);
    void recordDraw(UUID playerXId, UUID playerOId);
    void recordIncompleteGame(UUID playerXId, UUID playerOId);
    void recordGamePlayed(UUID playerId);
    List<UUID> getPlayerGames(UUID playerId);

}
