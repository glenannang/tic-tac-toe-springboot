package com.svi.tictactoe.util;

import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.enums.PlayerSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardUtil {

    public static List<PlayerSymbol> buildBoard(List<Move> moves) {

        // empty 9-position board
        List<PlayerSymbol> board = new ArrayList<>(Collections.nCopies(9, null));

        for (Move move : moves) {
            board.set(move.getPosition(), move.getSymbol());
        }

        return board;
    }
}