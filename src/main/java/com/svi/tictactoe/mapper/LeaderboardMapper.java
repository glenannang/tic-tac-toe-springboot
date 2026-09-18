package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.LeaderboardEntryResponse;
import com.svi.tictactoe.dto.response.LeaderboardResponse;
import com.svi.tictactoe.dto.response.PlayerRankResponse;
import com.svi.tictactoe.entity.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaderboardMapper {

    public LeaderboardEntryResponse toLeaderboardEntryResponse(Player player, int rank) {

        LeaderboardEntryResponse response = new LeaderboardEntryResponse();

        response.setRank(rank);
        response.setPlayerId(player.getPlayerId());
        response.setWins(player.getWins());
        response.setLosses(player.getLosses());
        response.setDraws(player.getDraws());
        response.setGamesPlayed(player.getGamesPlayed());
        response.setIncompleteGames(player.getIncompleteGames());

        return response;
    }

    public LeaderboardResponse toLeaderboardResponse(List<LeaderboardEntryResponse> entries) {

        LeaderboardResponse response = new LeaderboardResponse();
        response.setPlayers(entries);

        return response;
    }

    public PlayerRankResponse toPlayerRankResponse(Player player, int rank, int totalPlayers) {

        PlayerRankResponse response = new PlayerRankResponse();

        response.setRank(rank);
        response.setTotalPlayers(totalPlayers);
        response.setPlayerId(player.getPlayerId());
        response.setWins(player.getWins());
        response.setLosses(player.getLosses());
        response.setDraws(player.getDraws());
        response.setGamesPlayed(player.getGamesPlayed());
        response.setIncompleteGames(player.getIncompleteGames());

        return response;
    }






}
