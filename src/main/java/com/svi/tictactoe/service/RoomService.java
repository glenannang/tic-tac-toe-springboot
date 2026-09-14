package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.response.JoinRoomResponse;

public interface RoomService {

    JoinRoomResponse joinRoom(String roomCode, JoinRoomRequest request);
}