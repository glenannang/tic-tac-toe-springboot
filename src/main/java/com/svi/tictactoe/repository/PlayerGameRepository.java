package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.PlayerGame;
import com.svi.tictactoe.entity.PlayerGameKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerGameRepository extends CassandraRepository<PlayerGame, PlayerGameKey> {
}

