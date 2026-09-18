package com.svi.tictactoe.dto.response.game;

import com.svi.tictactoe.enums.PlayerSymbol;

import java.util.List;

public class BoardStatusResponse {

    private List<PlayerSymbol> board;

    public BoardStatusResponse(List<PlayerSymbol> board) {
        this.board = board;
    }

    public List<PlayerSymbol> getBoard() {
        return board;
    }

    public void setBoard(List<PlayerSymbol> board) {
        this.board = board;
    }
}