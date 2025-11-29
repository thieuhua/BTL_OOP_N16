package nhom16oop.ui;

import nhom16oop.constants.PieceColor;
import nhom16oop.core.model.ChessPiece;

public interface PlayerPanelListener {
    void onScoreUpdated(PieceColor color, int score);

    void onPieceCaptured(PieceColor capturerColor, ChessPiece capturedPiece);

    void onTurnChanged(PieceColor currentPlayerColor);

    void onTimerUpdate(PieceColor color, long timeRemaining);

    void onTimerVisibilityChanged(boolean visible);
}
