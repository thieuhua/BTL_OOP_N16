package nhom16oop.ui;

import nhom16oop.constants.GameMode;
import nhom16oop.constants.PieceColor;
import nhom16oop.game.ChessBoard;
import nhom16oop.game.ChessController;
import nhom16oop.ui.components.panels.ChessToolbar;
import nhom16oop.ui.components.panels.MoveHistoryPanel;
import nhom16oop.ui.components.panels.PlayerPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the user interface for the chess game.
 */
public class ChessUI {
    private final ChessController chessController;
    private final JFrame frame;
    private static final Logger logger = LoggerFactory.getLogger(ChessUI.class);

    /**
     * Constructs a ChessUI with the specified game mode and player color.
     *
     * @param gameMode      the game mode (e.g., PLAYER_VS_PLAYER, PLAYER_VS_AI, PUZZLE_MODE)
     * @param selectedColor the color chosen for the human player in PLAYER_VS_AI mode
     */
    public ChessUI(int gameMode, PieceColor selectedColor) {
        this.chessController = new ChessController();
        configureGame(this.chessController, gameMode, selectedColor);
        this.frame = new JFrame("Chess Game");
        chessController.setFrame(frame);
        setupUI();
    }


/**
 * Constructor cho Puzzle Mode
 */
    public ChessUI(int gameMode, PieceColor selectedColor, String puzzleFEN, int maxMoves) {
        this.chessController = new ChessController();
        
        if (gameMode == GameMode.PUZZLE_MODE) {
            chessController.setPuzzleMode(puzzleFEN, maxMoves);
        } else {
            configureGame(this.chessController, gameMode, selectedColor);
        }
        
        this.frame = new JFrame("Chess Game - Puzzle Mode");
        chessController.setFrame(frame);
        setupUI();
    }

    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        ChessBoard chessBoard = new ChessBoard(chessController);
        chessController.setChessBoard(chessBoard);
        mainPanel.add(chessBoard, BorderLayout.CENTER);

        logger.info("Game mode selected {}", chessController.getGameMode());

        PlayerPanel whitePlayerPanel;
        PlayerPanel blackPlayerPanel;
        switch (chessController.getGameMode()) {
            case GameMode.PUZZLE_MODE -> {
                PieceColor playerColor = chessController.getHumanPlayerColor();
                whitePlayerPanel = new PlayerPanel("You", playerColor, "images/player.png", chessController);
                mainPanel.add(whitePlayerPanel, playerColor.isWhite() ? BorderLayout.WEST : BorderLayout.EAST);
                blackPlayerPanel = new PlayerPanel("AI", playerColor.isWhite() ? PieceColor.BLACK : PieceColor.WHITE, "images/stockfish.png", chessController);
                mainPanel.add(blackPlayerPanel, playerColor.isWhite() ? BorderLayout.EAST : BorderLayout.WEST);
            }
            case GameMode.PLAYER_VS_AI -> {
                PieceColor playerColor = chessController.getHumanPlayerColor();
                whitePlayerPanel = new PlayerPanel("You", playerColor, "images/player.png", chessController);
                mainPanel.add(whitePlayerPanel, playerColor.isWhite() ? BorderLayout.WEST : BorderLayout.EAST);
                blackPlayerPanel = new PlayerPanel("AI", playerColor.isWhite() ? PieceColor.BLACK : PieceColor.WHITE, "images/stockfish.png", chessController);
                mainPanel.add(blackPlayerPanel, playerColor.isWhite() ? BorderLayout.EAST : BorderLayout.WEST);
            }
            case GameMode.PLAYER_VS_PLAYER -> {
                whitePlayerPanel = new PlayerPanel("Player 1", PieceColor.WHITE, "images/player.png", chessController);
                mainPanel.add(whitePlayerPanel, BorderLayout.WEST);
                blackPlayerPanel = new PlayerPanel("Player 2", PieceColor.BLACK, "images/player.png", chessController);
                mainPanel.add(blackPlayerPanel, BorderLayout.EAST);
            }
            default -> {
                logger.info("No game mode were selected!");
                return;
            }
        }

        if (chessController.isPuzzleMode()) {
            JPanel puzzleInfoPanel = createPuzzleInfoPanel();
            mainPanel.add(puzzleInfoPanel, BorderLayout.NORTH);
        } else {
            MoveHistoryPanel moveHistoryPanel = new MoveHistoryPanel(chessController);
            chessController.addHistoryChangeListener(moveHistoryPanel);
            mainPanel.add(moveHistoryPanel, BorderLayout.NORTH);
        }

        ChessToolbar toolbar = new ChessToolbar(chessController, chessController.getBoardUI());
        mainPanel.add(toolbar, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Ask user whether to save before quitting the application
                int choice = JOptionPane.showConfirmDialog(frame,
                        "Do you want to save the current game before quitting?",
                        "Save before Quit",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    // abort close
                    // Because default close is DISPOSE, we need to cancel disposal.
                    // to cancel, we do nothing here (window will not be disposed automatically until user confirms)
                    // but to be safe, we can explicitly call setDefaultCloseOperation again
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    return;
                }

                if (choice == JOptionPane.YES_OPTION) {
                    boolean saved = false;
                    try {
                        saved = chessController.saveCurrentGame();
                    } catch (Throwable ex) {
                        // fallback or inform user
                        logger.warn("Auto-save failed: {}", ex.getMessage(), ex);
                        String filename = JOptionPane.showInputDialog(frame, "Auto-save failed. Enter save name:", "Save Game", JOptionPane.PLAIN_MESSAGE);
                        if (filename != null && !filename.trim().isEmpty()) {
                            try {
                                saved = chessController.saveCurrentGame(filename.trim());
                            } catch (Throwable ex2) {
                                logger.error("Fallback save failed: {}", ex2.getMessage(), ex2);
                                JOptionPane.showMessageDialog(frame, "Save failed: " + ex2.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                                // abort quitting because save failed
                                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                return;
                            }
                        } else {
                            // user cancelled naming -> abort quit
                            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                            return;
                        }
                    }
                }

                // If we reach here, either user chose NO or save succeeded -> proceed to shutdown
                logger.info("Shutting down chess controller and exiting app");
                chessController.shutdown();
                // allow disposal and exit
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                // dispose and exit
                frame.dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Displays the chess game UI.
     */
    public void show() {
        frame.setVisible(true);
    }

    private void configureGame(ChessController controller, int mode, PieceColor playerColor) {
        switch (mode) {
            case GameMode.PLAYER_VS_PLAYER -> controller.setPlayerVsPlayer();
            case GameMode.PLAYER_VS_AI -> controller.setPlayerVsAI(playerColor);
            case GameMode.PUZZLE_MODE-> {}
        }
    }

    /**
     * Tạo panel hiển thị thông tin puzzle
     */
    private JPanel createPuzzleInfoPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(139, 69, 19));
        panel.setPreferredSize(new Dimension(800, 95));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("🧩 PUZZLE MODE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Moves counter label (sẽ update động)
        JLabel movesLabel = new JLabel("", SwingConstants.CENTER);
        movesLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        movesLabel.setForeground(Color.WHITE);
        movesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Objective label
        JLabel objectiveLabel = new JLabel("🎯 Mục tiêu: Chiếu hết đối thủ!", SwingConstants.CENTER);
        objectiveLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        objectiveLabel.setForeground(new Color(245, 245, 220));
        objectiveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(movesLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(objectiveLabel);
        panel.add(Box.createVerticalGlue());
        
        // Update moves label khi game state thay đổi
        chessController.addGameStateListener(() -> {
            SwingUtilities.invokeLater(() -> {
                int remaining = chessController.getPuzzleRemainingMoves();
                int total = chessController.getPuzzleMaxMoves();
                int current = chessController.getPuzzleCurrentMoves();
                
                movesLabel.setText(String.format("Đã đi: %d/%d  |  Còn lại: %d nước", 
                                                current, total, remaining));
                
                // Đổi màu warning khi sắp hết
                if (remaining == 0) {
                    movesLabel.setForeground(new Color(255, 50, 50));
                } else if (remaining == 1) {
                    movesLabel.setForeground(new Color(255, 100, 100));
                } else if (remaining <= 2) {
                    movesLabel.setForeground(new Color(255, 200, 0));
                } else {
                    movesLabel.setForeground(Color.WHITE);
                }
            });
        });
        
        // Trigger initial update
        SwingUtilities.invokeLater(() -> {
            int remaining = chessController.getPuzzleRemainingMoves();
            int total = chessController.getPuzzleMaxMoves();
            movesLabel.setText(String.format("Đã đi: 0/%d  |  Còn lại: %d nước", total, remaining));
        });
        
        return panel;
    }
}
