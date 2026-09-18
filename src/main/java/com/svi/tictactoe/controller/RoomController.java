package com.svi.tictactoe.controller;

import com.svi.tictactoe.dto.request.room.CreateRoomRequest;
import com.svi.tictactoe.dto.request.room.JoinRoomRequest;
import com.svi.tictactoe.dto.request.room.LeaveRoomRequest;
import com.svi.tictactoe.dto.request.room.RematchRequest;
import com.svi.tictactoe.dto.response.room.LeaveRoomResponse;
import com.svi.tictactoe.dto.response.room.RematchResponse;
import com.svi.tictactoe.dto.response.room.RoomResponse;
import com.svi.tictactoe.dto.response.room.RoomStatusResponse;
import com.svi.tictactoe.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {

        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<RoomResponse> joinRoom(@PathVariable String roomCode, @Valid @RequestBody JoinRoomRequest request) {

        RoomResponse response = roomService.joinRoom(roomCode, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomStatusResponse> getRoomStatus (@PathVariable String roomCode) {
        RoomStatusResponse response = roomService.getRoomStatus(roomCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomCode}/leave")
    public ResponseEntity<LeaveRoomResponse> leaveRoom(@PathVariable String roomCode, @Valid @RequestBody LeaveRoomRequest request) {

        LeaveRoomResponse response = roomService.leaveRoom(roomCode, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{roomCode}/rematch")
    public ResponseEntity<RematchResponse> rematch(@PathVariable String roomCode, @Valid @RequestBody RematchRequest request) {
        RematchResponse response = roomService.rematch(roomCode, request);
        return ResponseEntity.ok(response);
    }



}