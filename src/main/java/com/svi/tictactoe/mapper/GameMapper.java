package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.CreateGameResponse;
import com.svi.tictactoe.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public CreateGameResponse toCreateGameResponse(Game game, String message) {

        CreateGameResponse response = new CreateGameResponse();

        response.setGameId(game.getGameId());
        response.setMessage(message);

        return response;
    }
}