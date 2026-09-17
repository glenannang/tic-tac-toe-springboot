package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.CreateGameResponse;
import com.svi.tictactoe.dto.response.GameStatusResponse;
import com.svi.tictactoe.dto.response.RemoveGameResponse;
import com.svi.tictactoe.entity.Game;
import com.svi.tictactoe.enums.PlayerSymbol;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapper {

    public CreateGameResponse toCreateGameResponse(Game game, String message) {

        CreateGameResponse response = new CreateGameResponse();

        response.setGameId(game.getGameId());
        response.setMessage(message);

        return response;
    }

    public GameStatusResponse toGameStatusResponse(Game game, List<PlayerSymbol> board, PlayerSymbol nextTurn, int moveCount) {

        GameStatusResponse response = new GameStatusResponse();

        response.setGameId(game.getGameId());
        response.setStatus(game.getStatus());
        response.setBoard(board);
        response.setNextTurn(nextTurn);
        response.setMoveCount(moveCount);
        response.setWinnerId(game.getWinnerId());
        response.setResult(game.getResult());

        return response;
    }

    public RemoveGameResponse toRemoveGameResponse(String message) {

        RemoveGameResponse response = new RemoveGameResponse();
        response.setMessage(message);

        return response;
    }




}