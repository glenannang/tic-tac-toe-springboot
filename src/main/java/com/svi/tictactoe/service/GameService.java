package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.response.AddMoveResponse;

import java.util.UUID;

public interface GameService {

    UUID createGame(String roomCode);
    AddMoveResponse addMove(UUID gameId,AddMoveRequest request);
}
