package com.svi.tictactoe.entity;

import com.svi.tictactoe.enums.RoomStatus;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("rooms_by_code")
public class Room {

    @PrimaryKey
    private RoomKey key;

    @Column("host_player_id")
    private UUID hostPlayerId;

    @Column("guest_player_id")
    private UUID guestPlayerId;

    @Column("game_id")
    private UUID gameId;

    @Column("status")
    private RoomStatus status;

    @Column("host_rematch")
    private boolean hostRematch;

    @Column("guest_rematch")
    private boolean guestRematch;

    @Column("updated_at")
    private Instant updatedAt;

    public Room() {
    }

    public RoomKey getKey() {
        return key;
    }

    public void setKey(RoomKey key) {
        this.key = key;
    }

    public UUID getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(UUID hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public UUID getGuestPlayerId() {
        return guestPlayerId;
    }

    public void setGuestPlayerId(UUID guestPlayerId) {
        this.guestPlayerId = guestPlayerId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public boolean isHostRematch() {
        return hostRematch;
    }

    public void setHostRematch(boolean hostRematch) {
        this.hostRematch = hostRematch;
    }

    public boolean isGuestRematch() {
        return guestRematch;
    }

    public void setGuestRematch(boolean guestRematch) {
        this.guestRematch = guestRematch;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}