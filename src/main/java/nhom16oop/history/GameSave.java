package nhom16oop.history;

import nhom16oop.core.model.BoardState;
import nhom16oop.game.ChessController;
import nhom16oop.utils.ChessNotationUtils;

import java.time.Instant;

public class GameSave {
    // private String id;
    private String name;
    private String fen; // store board as FEN if you have FEN utils
    public int gameMode;
    // private String movesSerialized; // optional: list of moves (e.g., algebraic or SAN)
    private long timestamp;

    public GameSave() {}

    public static GameSave fromController(ChessController controller) {
        GameSave s = new GameSave();
        s.gameMode = controller.getGameMode();
        s.timestamp = Instant.now().toEpochMilli();
        s.name = "save-" + s.timestamp;
        // try to get FEN or BoardState
        try {
            if (controller.getBoardManager() != null) {
                BoardState state = controller.getBoardManager().getCurrentBoardState();
                // assume you have ChessNotationUtils or BoardState.toFEN()
                s.fen = ChessNotationUtils.getFEN(state);
            }
            // if (controller.getHistoryManager() != null) {
            //     s.movesSerialized = controller.getHistoryManager().serializeCurrentHistory();
            // }
        } catch (Exception ex) {
            // best-effort
        }
        return s;
    }

    public void applyToController(ChessController controller) {
        // apply fen and moves to controller
        try {
            if (fen != null && !fen.isEmpty()) {
                controller.getBoardManager().loadFromFEN(fen); // implement loadFromFEN if missing
            }
            // if (movesSerialized != null && !movesSerialized.isEmpty() && controller.getHistoryManager() != null) {
            //     controller.getHistoryManager().loadFromSerialized(movesSerialized);
            // }
        } catch (Exception ex) {
            // ignore or log
        }
    }

    // getters/setters...
    public void setName(String n) { this.name = n; }
    public String getName() { return name; }
}
