package nhom16oop.ui.components.panels;

import nhom16oop.constants.GameMode;
import nhom16oop.game.ChessController;
import nhom16oop.game.GameActionManager;
import nhom16oop.game.GameStateListener;
import nhom16oop.ui.board.ChessBoardUI;
import nhom16oop.ui.components.dialogs.ResignDialog;
import nhom16oop.utils.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessToolbar extends JPanel implements GameStateListener {

    private static final Logger logger = LoggerFactory.getLogger(ChessToolbar.class);

    private final ChessController chessController;
    private final ChessBoardUI boardUI;
    private final GameActionManager actionManager;
    private final List<ButtonConfig> buttonConfigs;
    private final Map<ButtonConfig, JButton> buttonMap;

    private static final int ICON_SIZE = 24;
    private static final int TOOLBAR_HEIGHT = 50;
    private static final Color BACKGROUND_COLOR = new Color(40, 40, 40);
    private static final int BORDER_SIZE = 5;

    public ChessToolbar(ChessController controller, ChessBoardUI boardUI) {
        this.chessController = controller;
        this.boardUI = boardUI;
        this.actionManager = controller.getActionManager();
        this.buttonConfigs = new ArrayList<>();
        this.buttonMap = new HashMap<>();

        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        setPreferredSize(new Dimension(0, TOOLBAR_HEIGHT));

        initializeButtonConfigs();
        updateButtonLayout();

        initializeGameStateListener();
        logButtonStates();
    }

    private void initializeGameStateListener() {
        chessController.addGameStateListener(this);
        logger.debug("ChessToolbar registered as GameStateListener");
    }

    private void initializeButtonConfigs() {
        // --- NEW: Back button (save -> close -> launch launcher)
        buttonConfigs.add(new ButtonConfig("Back", "images/left-arrow.png", e -> {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            int choice = JOptionPane.showOptionDialog(parent,
                    "Do you want to save the current game before returning to launcher?",
                    "Return to Launcher",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Save & Return", "Return without Saving", "Cancel"},
                    "Save & Return");

            if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                return;
            }

            // If user chose to save
            if (choice == JOptionPane.YES_OPTION) {
                boolean saved = false;
                try {
                    // TODO: phai fix doan nay
                    // prefer controller-level save API (implement if missing)
                    // saved = chessController.saveCurrentGameToHistory();
                } catch (Throwable ex) {
                    logger.warn("Save via chessController failed: {}", ex.getMessage(), ex);
                }

                if (!saved) {
                    // fallback: ask user for filename and try saving FEN (if controller supports)
                    String filename = JOptionPane.showInputDialog(parent, "Save failed or not supported automatically.\nEnter save name:", "Save Game", JOptionPane.PLAIN_MESSAGE);
                    if (filename != null && !filename.trim().isEmpty()) {
                        try {
                            // TODO phai them api nay
                            // saved = chessController.saveCurrentGameToHistory(filename.trim());
                        } catch (Throwable ex) {
                            logger.error("Fallback save failed: {}", ex.getMessage(), ex);
                            JOptionPane.showMessageDialog(parent, "Save failed: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        // user cancelled filename => abort going back
                        return;
                    }
                }
            }

            // Finally close game UI and launch launcher
            SwingUtilities.invokeLater(() -> {
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win != null) {
                    win.dispose();
                }
                // Launch a fresh launcher (ChessLauncher.launch() should show launcher dialog)
                // We call this on EDT to be safe
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        nhom16oop.game.ChessLauncher.launch();
                    } catch (Exception ex) {
                        logger.error("Failed to relaunch ChessLauncher: {}", ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(null, "Unable to open launcher: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            });

        }, () -> true));

        // existing buttons...
        buttonConfigs.add(new ButtonConfig("Flip Board", "images/flip-board.png", e -> boardUI.flipBoard(), () -> true));

        buttonConfigs.add(new ButtonConfig("Resign", "images/resign.png", e -> {
            ResignDialog dialog = new ResignDialog((Frame) SwingUtilities.getWindowAncestor(this), "Are you sure you want to resign?");
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                actionManager.resignGame();
            }
        }, () -> chessController.getGameMode() != GameMode.PUZZLE_MODE && !chessController.isGameEnded()));

        buttonConfigs.add(new ButtonConfig("Show hint", "images/hint.png", e -> actionManager.showHint(), () -> chessController.getGameMode() == GameMode.PLAYER_VS_AI && !chessController.isGameEnded()));

        buttonConfigs.add(new ButtonConfig("Move Back", "images/back.png", e -> actionManager.undoMove(), () -> !chessController.isGameEnded() && chessController.getHistoryManager() != null));

        buttonConfigs.add(new ButtonConfig("Move Forward", "images/forward.png", e -> actionManager.redoMove(), () -> !chessController.isGameEnded() && chessController.getHistoryManager() != null));
    }

    private void updateButtonLayout() {
        int visibleCount = 0;
        for (ButtonConfig config : buttonConfigs) {
            if (config.isVisible.getAsBoolean()) {
                visibleCount++;
            }
        }

        removeAll();
        setLayout(new GridLayout(1, visibleCount > 0 ? visibleCount : 1));
        buttonMap.clear();

        for (ButtonConfig config : buttonConfigs) {
            if (config.isVisible.getAsBoolean()) {
                JButton button = createButton(config);
                buttonMap.put(config, button);
                add(button);
            }
        }

        updateButtonStates();
        logger.debug("Updated toolbar layout: visible buttons = {}", visibleCount);
        logButtonStates();

        revalidate();
        repaint();
    }

    private JButton createButton(ButtonConfig config) {
        Image iconImage = ImageLoader.getImage(config.iconPath, ICON_SIZE, ICON_SIZE);
        JButton button = new JButton(iconImage != null ? new ImageIcon(iconImage) : new JButton(config.tooltip).getIcon());
        button.setToolTipText(config.tooltip);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            if (!button.isEnabled()) {
                return;
            }
            setAllButtonsEnabled(false);
            SwingUtilities.invokeLater(() -> {
                try {
                    config.action.actionPerformed(e);
                } finally {
                    updateButtonLayout();
                }
            });
        });

        return button;
    }

    public void updateButtonStates() {
        for (ButtonConfig config : buttonConfigs) {
            JButton button = buttonMap.get(config);
            if (button != null) {
                boolean shouldBeEnabled = isButtonEnabled(config);
                button.setEnabled(shouldBeEnabled);
            }
        }
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            updateButtonLayout();
            logButtonStates();
        });
    }

    @Override
    public void onGameStateChanged() {
        logger.debug("Received game state change notification");
        refresh();
    }

    private boolean isButtonEnabled(ButtonConfig config) {
         if (chessController.isPuzzleMode()) {
            switch (config.tooltip) {
                case "Move Back":
                case "Move Forward":
                   return false;
                 default:
                    break;
            }
        }

        if (chessController.getHistoryManager() == null) {
            return false;
        }
        return switch (config.tooltip) {
            case "Move Back" -> !chessController.getHistoryManager().getUndoStack().isEmpty();
            case "Move Forward" -> !chessController.getHistoryManager().getRedoStack().isEmpty();
            case "Show hint" -> chessController.getHumanPlayerColor() == chessController.getBoardManager().getCurrentPlayerColor();
            default -> config.isVisible.getAsBoolean();
        };
    }

    private void setAllButtonsEnabled(boolean enabled) {
        for (JButton button : buttonMap.values()) {
            button.setEnabled(enabled);
        }
    }

    private void logButtonStates() {
        boolean historyManagerExists = chessController.getHistoryManager() != null;
        int undoStackSize = historyManagerExists ? chessController.getHistoryManager().getUndoStack().size() : 0;
        int redoStackSize = historyManagerExists ? chessController.getHistoryManager().getRedoStack().size() : 0;
        boolean gameEnded = chessController.isGameEnded();

        logger.debug("Toolbar state: historyManager = {}, undoStackSize = {}, redoStackSize = {}, gameEnded = {}", historyManagerExists, undoStackSize, redoStackSize, gameEnded);
    }

    private record ButtonConfig(String tooltip, String iconPath, ActionListener action, java.util.function.BooleanSupplier isVisible) {
    }
}
