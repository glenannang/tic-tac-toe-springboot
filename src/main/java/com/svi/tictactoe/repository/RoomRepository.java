package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CassandraRepository<Room, String> {
}

