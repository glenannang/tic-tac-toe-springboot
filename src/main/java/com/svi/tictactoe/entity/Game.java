package com.svi.tictactoe.entity;

import com.svi.tictactoe.enums.GameResult;
import com.svi.tictactoe.enums.GameStatus;
import com.svi.tictactoe.enums.PlayerSymbol;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("games")
public class Game {

    @PrimaryKey
    @Column("game_id")
    private UUID gameId;

    @Column("room_code")
    private String roomCode;

    @Column("player_x_id")
    private UUID playerXId;

    @Column("player_o_id")
    private UUID playerOId;

    @Column("board")
    private String board;

    @Column("status")
    private GameStatus status;

    @Column("next_turn")
    private PlayerSymbol nextTurn;

    @Column("winner_id")
    private UUID winnerId;

    @Column("result")
    private GameResult result;

    @Column("move_count")
    private int moveCount;

    @Column("created_at")
    private Instant createdAt;

    @Column("ended_at")
    private Instant endedAt;

    public Game() {
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public UUID getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(UUID playerXId) {
        this.playerXId = playerXId;
    }

    public UUID getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(UUID playerOId) {
        this.playerOId = playerOId;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public PlayerSymbol getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(PlayerSymbol nextTurn) {
        this.nextTurn = nextTurn;
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

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }
}