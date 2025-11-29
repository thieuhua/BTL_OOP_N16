package nhom16oop.history;

import nhom16oop.core.model.BoardState;
import nhom16oop.game.ChessController;
import nhom16oop.utils.ChessNotationUtils;

import java.time.Instant;

public class GameSave {
    // private String id;
    public String name;
    public String fen;
    public int gameMode;
    public boolean chosenWhite;
    public long whiteTimeRemaining;
    public long blackTimeRemaining;
    
    // private String movesSerialized; // optional: list of moves (e.g., algebraic or SAN)
    public long timestamp;

    public GameSave() {}

    // getters/setters...
    public void setName(String n) { this.name = n; }
    public String getName() { return name; }
}
