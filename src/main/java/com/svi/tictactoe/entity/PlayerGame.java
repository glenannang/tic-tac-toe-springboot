package com.svi.tictactoe.entity;

import com.svi.tictactoe.enums.PlayerGameResult;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("games_by_player")
public class PlayerGame {

    @PrimaryKey
    private PlayerGameKey key;

    @Column("result")
    private String result;

    public PlayerGame() {
    }

    public PlayerGameKey getKey() {
        return key;
    }

    public void setKey(PlayerGameKey key) {
        this.key = key;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}