package nhom16oop.game;

import nhom16oop.constants.GameMode;
import nhom16oop.constants.PieceColor;
import nhom16oop.ui.ChessUI;
import nhom16oop.ui.components.dialogs.GameModeSelectionDialog;

public class ChessLauncher {
    public static void launch() {
        GameModeSelectionDialog dialog = new GameModeSelectionDialog(null);
        dialog.setVisible(true);

        int selectedMode = dialog.getSelectedMode();
        PieceColor selectedColor = dialog.getSelectedColor();

        if (selectedMode == GameMode.PUZZLE_MODE) {
            String puzzleFEN = dialog.getSelectedPuzzleFEN();
            int maxMoves = dialog.getSelectedPuzzleMaxMoves();
            
            if (puzzleFEN != null && !puzzleFEN.isEmpty()) {
                ChessUI chessUI = new ChessUI(selectedMode, selectedColor, puzzleFEN, maxMoves);
                chessUI.show();
            } else {
                // Nếu không chọn puzzle, launch lại
                launch();
            }
        } 
        else {
            // PLAYER_VS_PLAYER hoặc PLAYER_VS_AI
            ChessUI chessUI = new ChessUI(selectedMode, selectedColor);
            chessUI.show();
        }
    }
}
