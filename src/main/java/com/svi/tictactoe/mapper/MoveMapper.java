package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.entity.Move;
import org.springframework.stereotype.Component;

@Component
public class MoveMapper {

    public AddMoveResponse toAddMoveResponse(Move move, String message){

        AddMoveResponse response = new AddMoveResponse();
        response.setMessage(message);
        response.setMoveNumber(move.getKey().getMoveNumber());
        return response;
    }

}
