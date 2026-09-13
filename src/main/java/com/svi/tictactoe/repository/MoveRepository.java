package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.entity.MoveKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends CassandraRepository<Move, MoveKey> {
}
