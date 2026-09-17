package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.request.CreateRoomRequest;
import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.request.LeaveRoomRequest;
import com.svi.tictactoe.dto.response.LeaveRoomResponse;
import com.svi.tictactoe.dto.response.RoomResponse;
import com.svi.tictactoe.dto.response.RoomStatusResponse;

public interface RoomService {

    RoomResponse joinRoom(String roomCode, JoinRoomRequest request);
    RoomResponse createRoom(String roomCode, CreateRoomRequest request);
    RoomStatusResponse getRoomStatus(String roomCode);
    LeaveRoomResponse leaveRoom(String roomCode, LeaveRoomRequest request);
}