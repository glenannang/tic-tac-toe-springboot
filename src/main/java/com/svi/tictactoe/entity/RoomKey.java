package com.svi.tictactoe.entity;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@PrimaryKeyClass
public class RoomKey implements Serializable {

    @PrimaryKeyColumn(
            name = "room_code",
            type = PrimaryKeyType.PARTITIONED
    )
    private String roomCode;

    @PrimaryKeyColumn(
            name = "created_at",
            ordinal = 0,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING
    )
    private Instant createdAt;

    public RoomKey() {
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}