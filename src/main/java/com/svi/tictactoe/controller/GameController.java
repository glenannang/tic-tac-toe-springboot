package com.svi.tictactoe.controller;


import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.request.CreateGameRequest;
import com.svi.tictactoe.dto.response.*;
import com.svi.tictactoe.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController (GameService gameService){
        this.gameService  = gameService;
    }

    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(@Valid @RequestBody CreateGameRequest request) {

        CreateGameResponse response = gameService.createGame(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{gameId}/moves")
    public ResponseEntity<AddMoveResponse> addMoves(@PathVariable UUID gameId, @Valid @RequestBody AddMoveRequest request) {
        AddMoveResponse response = gameService.addMove(gameId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStatusResponse> getGameStatus(
            @PathVariable UUID gameId) {

        GameStatusResponse response = gameService.getGameStatus(gameId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/remove")
    public ResponseEntity<RemoveGameResponse> removeGame(@PathVariable UUID gameId) {
        RemoveGameResponse response = gameService.removeGame(gameId);
        return ResponseEntity.ok(response);
    }

    
}
