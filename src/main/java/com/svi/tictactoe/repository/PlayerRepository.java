package com.svi.tictactoe.repository;

import com.svi.tictactoe.entity.Player;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface PlayerRepository extends CassandraRepository<Player, UUID> {


}
