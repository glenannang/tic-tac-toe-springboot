package com.svi.tictactoe.dto.response.game;

import com.svi.tictactoe.enums.GameResult;
import com.svi.tictactoe.enums.GameStatus;
import com.svi.tictactoe.enums.PlayerSymbol;

import java.util.List;
import java.util.UUID;

public class GameStatusResponse {

    private UUID gameId;
    private GameStatus status;
    private List<PlayerSymbol> board;
    private PlayerSymbol nextTurn;
    private int moveCount;
    private UUID winnerId;
    private GameResult result;

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<PlayerSymbol> getBoard() {
        return board;
    }

    public void setBoard(List<PlayerSymbol> board) {
        this.board = board;
    }

    public PlayerSymbol getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(PlayerSymbol nextTurn) {
        this.nextTurn = nextTurn;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
}