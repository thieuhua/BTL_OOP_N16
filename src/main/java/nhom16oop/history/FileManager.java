package nhom16oop.history;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// public class GameSave {
//     // private String id;
//     private String name;
//     private String fen; // store board as FEN if you have FEN utils
//     public int gameMode;
//     // private String movesSerialized; // optional: list of moves (e.g., algebraic or SAN)
//     private long timestamp;

public class FileManager {
    private static final String SAVE_DIR = "saves"; // relative to working dir
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // existing fields...

    static public void save(GameSave save) {
        // minimal: write JSON to a configured folder with filename save.getName() + ".json"
        String json = GSON.toJson(save);
        // write json to file
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get(SAVE_DIR);
            java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path file = dir.resolve(save.getName() + ".json");
            java.nio.file.Files.write(file, json.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    static public GameSave loadLatest() {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get(SAVE_DIR);
            if (!java.nio.file.Files.exists(dir) || !java.nio.file.Files.isDirectory(dir)) {
                return null;
            }
            try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.list(dir)) {
                java.util.Optional<java.nio.file.Path> latest = stream
                    .filter(p -> p.toString().toLowerCase().endsWith(".json"))
                    .filter(p -> java.nio.file.Files.isRegularFile(p))
                    .max((a, b) -> {
                        try {
                            java.nio.file.attribute.FileTime ta = java.nio.file.Files.getLastModifiedTime(a);
                            java.nio.file.attribute.FileTime tb = java.nio.file.Files.getLastModifiedTime(b);
                            return ta.compareTo(tb);
                        } catch (java.io.IOException e) {
                            return 0;
                        }
                    });

                if (!latest.isPresent()) {
                    return null;
                }

                byte[] bytes = java.nio.file.Files.readAllBytes(latest.get());
                String json = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                return GSON.fromJson(json, GameSave.class);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
