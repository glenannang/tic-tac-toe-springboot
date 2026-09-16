package com.svi.tictactoe.service;

import java.util.UUID;

public interface GameService {

    UUID createGame(String roomCode, UUID playerXId, UUID playerOId);
}
