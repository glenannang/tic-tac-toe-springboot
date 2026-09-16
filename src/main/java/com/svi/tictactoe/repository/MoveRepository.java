package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.entity.MoveKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MoveRepository extends CassandraRepository<Move, MoveKey> {
    List<Move> findByKeyGameId(UUID gameId);
}
