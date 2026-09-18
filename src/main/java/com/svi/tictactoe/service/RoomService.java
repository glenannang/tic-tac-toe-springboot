package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.room.CreateRoomRequest;
import com.svi.tictactoe.dto.request.room.JoinRoomRequest;
import com.svi.tictactoe.dto.request.room.LeaveRoomRequest;
import com.svi.tictactoe.dto.request.room.RematchRequest;
import com.svi.tictactoe.dto.response.room.LeaveRoomResponse;
import com.svi.tictactoe.dto.response.room.RematchResponse;
import com.svi.tictactoe.dto.response.room.RoomResponse;
import com.svi.tictactoe.dto.response.room.RoomStatusResponse;

public interface RoomService {

    RoomResponse joinRoom(String roomCode, JoinRoomRequest request);
    RoomResponse createRoom(CreateRoomRequest request);
    RoomStatusResponse getRoomStatus(String roomCode);
    LeaveRoomResponse leaveRoom(String roomCode, LeaveRoomRequest request);
    RematchResponse rematch(String roomCode, RematchRequest request);

}