package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.game.AddMoveRequest;
import com.svi.tictactoe.dto.request.game.CreateGameRequest;
import com.svi.tictactoe.dto.response.game.*;
import com.svi.tictactoe.entity.Game;

import java.util.UUID;

public interface GameService {

    CreateGameResponse createGame(CreateGameRequest request);
    Game createGameRecord(String roomCode, UUID playerXId, UUID playerOId);
    AddMoveResponse addMove(UUID gameId, AddMoveRequest request);
    GameStatusResponse getGameStatus(UUID gameId);
    RemoveGameResponse removeGame(UUID gameId);
    BoardStatusResponse getBoardStatus(UUID gameId);
    void abandonGame(UUID gameId);

}
