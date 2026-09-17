package com.svi.tictactoe.controller;

import com.svi.tictactoe.dto.request.CreateGameRequest;
import com.svi.tictactoe.dto.response.CreateGameResponse;
import com.svi.tictactoe.dto.response.CreatePlayerResponse;
import com.svi.tictactoe.service.GameService;
import com.svi.tictactoe.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/players")
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

}
