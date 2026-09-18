package com.svi.tictactoe.service.impl;

import com.svi.tictactoe.constant.ErrorMessages;
import com.svi.tictactoe.dto.response.leaderboard.LeaderboardEntryResponse;
import com.svi.tictactoe.dto.response.leaderboard.LeaderboardResponse;
import com.svi.tictactoe.dto.response.leaderboard.PlayerRankResponse;
import com.svi.tictactoe.entity.Player;
import com.svi.tictactoe.exception.player.PlayerDoesNotExistException;
import com.svi.tictactoe.mapper.LeaderboardMapper;
import com.svi.tictactoe.repository.PlayerRepository;
import com.svi.tictactoe.service.LeaderboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final PlayerRepository playerRepository;
    private final LeaderboardMapper leaderboardMapper;

    public LeaderboardServiceImpl(PlayerRepository playerRepository,
                                  LeaderboardMapper leaderboardMapper) {
        this.playerRepository = playerRepository;
        this.leaderboardMapper = leaderboardMapper;
    }

    @Override
    public LeaderboardResponse getLeaderboard() {

        List<Player> players = playerRepository.findAll();

        //compare wins
        players.sort((player1, player2) -> {return Integer.compare(player2.getWins(), player1.getWins());});

        //ranking
        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        int rank = 0;
        int previousWins = -1;

        for (int i = 0; i < players.size(); i++) {

            Player player = players.get(i);

            if (player.getWins() != previousWins) {
                rank = i + 1;
            }

            entries.add(leaderboardMapper.toLeaderboardEntryResponse(player, rank));
            previousWins = player.getWins();
        }

        return leaderboardMapper.toLeaderboardResponse(entries);

    }

    @Override
    public PlayerRankResponse getPlayerStatsAndRank(UUID playerId) {

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerDoesNotExistException(ErrorMessages.PLAYER_NOT_FOUND.getMessage()));

        List<Player> players = playerRepository.findAll();

        int rank = 1;

        for (Player otherPlayer : players) {
            if (otherPlayer.getWins() > player.getWins()) {
                rank++;
            }
        }

        int totalPlayers = players.size();
        return leaderboardMapper.toPlayerRankResponse(player, rank, totalPlayers);
    }
}
