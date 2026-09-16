package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.Room;
import com.svi.tictactoe.entity.RoomKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends CassandraRepository<Room, RoomKey> {

    Optional<Room> findFirstByKeyRoomCode(String roomCode);
}

