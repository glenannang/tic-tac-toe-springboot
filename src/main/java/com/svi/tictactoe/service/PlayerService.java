package com.svi.tictactoe.service;

import com.svi.tictactoe.dto.response.CreatePlayerResponse;

import java.util.UUID;

public interface PlayerService {

    CreatePlayerResponse createPlayer();
    void validatePlayerExists(UUID playerId);




}
