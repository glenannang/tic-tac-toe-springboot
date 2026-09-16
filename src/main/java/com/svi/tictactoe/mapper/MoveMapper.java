package com.svi.tictactoe.mapper;

import com.svi.tictactoe.dto.response.AddMoveResponse;
import com.svi.tictactoe.entity.Move;
import org.springframework.stereotype.Component;

@Component
public class MoveMapper {

    public AddMoveResponse toAddMoveResponse(int moveNumber, String message){

        AddMoveResponse response = new AddMoveResponse();
        response.setMessage(message);
        response.setMoveNumber(moveNumber);
        return response;

    }

}
