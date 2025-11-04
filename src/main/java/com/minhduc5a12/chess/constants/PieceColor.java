package com.minhduc5a12.chess.constants;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor getOpponent() {
        return this == WHITE ? BLACK : WHITE;
    }

    public boolean isWhite() {
        return this == WHITE;
    }

    public boolean isBlack() {
        return this == BLACK;
    }
}