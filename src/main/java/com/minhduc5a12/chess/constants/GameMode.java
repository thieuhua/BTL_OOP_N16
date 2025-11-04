package com.minhduc5a12.chess.constants;

public final class GameMode {
    public static final int PLAYER_VS_PLAYER = 1;
    public static final int PLAYER_VS_AI = 2;
    public static final int AI_VS_AI = 3;

    private GameMode() {
        throw new AssertionError("Cannot instantiate GameMode class");
    }
}