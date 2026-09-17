package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.AddMoveRequest;
import com.svi.tictactoe.dto.request.CreateGameRequest;
import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.dto.response.CreateGameResponse;
import com.svi.tictactoe.dto.response.GameStatusResponse;
import com.svi.tictactoe.dto.response.RemoveGameResponse;

import java.util.UUID;

public interface GameService {

    CreateGameResponse createGame(CreateGameRequest request);
    AddMoveResponse addMove(UUID gameId,AddMoveRequest request);
    GameStatusResponse getGameStatus(UUID gameId);
    RemoveGameResponse removeGame(UUID gameId);
}
