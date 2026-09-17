package com.svi.tictactoe.engine;

import com.svi.tictactoe.entity.Move;
import com.svi.tictactoe.enums.PlayerSymbol;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameEngine {

    private static final int[][] WINNING_COMBINATIONS = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    public boolean hasWon(List<Move> moves, PlayerSymbol symbol) {

        boolean[] occupiedPositions = new boolean[9];

        for (Move move : moves) {
            if (move.getSymbol() == symbol) {
                occupiedPositions[move.getPosition()] = true;
            }
        }

        for (int[] combination : WINNING_COMBINATIONS) {
            if (occupiedPositions[combination[0]]
                    && occupiedPositions[combination[1]]
                    && occupiedPositions[combination[2]]) {
                return true;
            }
        }

        return false;
    }

    public boolean isDraw(List<Move> moves) {
        return moves.size() == 9;
    }

    public PlayerSymbol getNextTurn(List<Move> moves) {
        return moves.size() % 2 == 0 ? PlayerSymbol.X : PlayerSymbol.O;
    }
}