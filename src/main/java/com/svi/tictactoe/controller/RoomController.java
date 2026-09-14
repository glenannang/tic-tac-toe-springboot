package com.svi.tictactoe.controller;

import com.svi.tictactoe.dto.request.JoinRoomRequest;
import com.svi.tictactoe.dto.response.JoinRoomResponse;
import com.svi.tictactoe.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(@PathVariable String roomCode, @Valid @RequestBody JoinRoomRequest request) {

        JoinRoomResponse response = roomService.joinRoom(roomCode, request);

        return ResponseEntity.ok(response);
    }
}