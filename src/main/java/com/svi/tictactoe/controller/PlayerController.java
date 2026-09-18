package com.svi.tictactoe.controller;

import com.svi.tictactoe.dto.response.player.CreatePlayerResponse;
import com.svi.tictactoe.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController (PlayerService playerService){
        this.playerService  = playerService;
    }

    @PostMapping
    public ResponseEntity<CreatePlayerResponse> createPlayer() {
        CreatePlayerResponse response = playerService.createPlayer();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{playerId}/games")
    public ResponseEntity<List<UUID>> getPlayerGames(@PathVariable UUID playerId) {

        List<UUID> games = playerService.getPlayerGames(playerId);
        return ResponseEntity.ok(games);
    }

}
