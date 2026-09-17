package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.CreatePlayerResponse;
import com.svi.tictactoe.entity.Player;
import org.springframework.stereotype.Component;


@Component
public class PlayerMapper {

    public CreatePlayerResponse toCreatePlayerResponse(Player player, String message) {
        CreatePlayerResponse response = new CreatePlayerResponse();
        response.setPlayerId(player.getPlayerId());
        response.setMessage(message);
        return response;
    }
}

