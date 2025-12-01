package nhom16oop.game;

import javax.swing.JOptionPane;

import nhom16oop.constants.GameMode;
import nhom16oop.constants.PieceColor;
import nhom16oop.history.FileManager;
import nhom16oop.history.GameSave;
import nhom16oop.ui.ChessUI;
import nhom16oop.ui.components.dialogs.GameModeSelectionDialog;

public class ChessLauncher {
    public static void launch() {
        GameModeSelectionDialog dialog = new GameModeSelectionDialog(null);
        dialog.setVisible(true);

        int selectedMode = dialog.getSelectedMode();
        GameSave gs = FileManager.loadLastestFromMode(selectedMode);
        if (gs != null) {
            int c = JOptionPane.showOptionDialog(null,
                    "Found saved games. Would you like to continue the last saved game or start a new one?",
                    "Resume saved game?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Continue last saved", "Start new"},
                    "Continue last saved");
            if (c == JOptionPane.YES_OPTION) {
                if (gs != null) {
                    ChessUI ui = new ChessUI(gs);
                    ui.show();
                    return;
                }
            }
        }
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
