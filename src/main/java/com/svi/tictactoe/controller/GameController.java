package com.svi.tictactoe.controller;


import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.request.CreateRoomRequest;
import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.dto.response.RoomResponse;
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
    @PostMapping("/{gameId}/moves")
    public ResponseEntity<RoomResponse> addMoves(@PathVariable UUID gameId, @Valid @RequestBody AddMoveRequest request) {
        AddMoveResponse response = gameService.addMove(gameId,request);
        return null;
    }

}
