package nhom16oop.ui.components.dialogs;

import nhom16oop.constants.GameMode;
import nhom16oop.constants.PieceColor;
import nhom16oop.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * A dialog for selecting the chess game mode and player color.
 */
public class GameModeSelectionDialog extends JDialog {
    private int selectedMode = GameMode.PLAYER_VS_PLAYER; // Default mode
    private PieceColor selectedColor = PieceColor.WHITE; // Default color
    private String selectedPuzzleFEN = null;
    private int selectedPuzzleMaxMoves = 0;
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 600;

    /**
     * Constructs a new game mode selection dialog.
     *
     * @param parent the parent {@code Frame} for the dialog
     */
    public GameModeSelectionDialog(Frame parent) {
        super(parent, "Select Game Mode", true);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));
        setResizable(false);

        // Handle window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        JPanel roundedPanel = createRoundedPanel();
        roundedPanel.setLayout(new BorderLayout(10, 10));
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        roundedPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        JLabel messageLabel = new JLabel("Select Game Mode", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setForeground(Color.WHITE);
        roundedPanel.add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        roundedPanel.add(buttonPanel, BorderLayout.CENTER);
        add(roundedPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Creates a rounded panel with a wood texture background.
     *
     * @return the rounded {@code JPanel}
     */
    private JPanel createRoundedPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Image woodTexture = ImageLoader.getImage("images/wood_texture.png", FRAME_WIDTH, FRAME_HEIGHT);
                g2d.drawImage(woodTexture, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(80, 40, 20));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2d.dispose();
            }
        };
    }

    /**
     * Creates a panel containing buttons for game mode selection.
     *
     * @return the button {@code JPanel}
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton pvpButton = createStyledButton("Player vs Player");
        pvpButton.addActionListener(this::onPlayerVsPlayerSelected);
        JButton pvaiButton = createStyledButton("Player vs Bot");
        pvaiButton.addActionListener(this::onPlayerVsAISelected);
        JButton puzzleButton = createStyledButton("Puzzle Mode");
        puzzleButton.addActionListener(this::onPuzzleModeSelected);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(pvpButton);
        pvpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(pvaiButton);
        pvaiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(puzzleButton);
        puzzleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createVerticalGlue());

        return buttonPanel;
    }

    /**
     * Creates a styled button with custom appearance.
     *
     * @param text the button text
     * @return the styled {@code JButton}
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isPressed() ? new Color(92, 51, 23) : getModel().isRollover() ? new Color(160, 82, 45) : new Color(139, 69, 19));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 50);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Handles the selection of Player vs. Player mode.
     *
     * @param e the action event
     */
    private void onPlayerVsPlayerSelected(ActionEvent e) {
        selectedMode = GameMode.PLAYER_VS_PLAYER;
        dispose();
    }

    /**
     * Handles the selection of Player vs. AI mode and shows the color selection dialog.
     *
     * @param e the action event
     */
    private void onPlayerVsAISelected(ActionEvent e) {
        selectedMode = GameMode.PLAYER_VS_AI;
        showColorSelectionDialog();
    }

    /**
     * Handles Puzzle Mode selection
     *  @param e the action event
     */
    private void onPuzzleModeSelected(ActionEvent e) {
        selectedMode = GameMode.PUZZLE_MODE;
        showInlinePuzzleSelection();
    }

    /**
     * Hiển thị dialog chọn puzzle
     */
    private void showInlinePuzzleSelection() {
        // Định nghĩa các puzzle mẫu (bạn có thể mở rộng hoặc load từ file)
        Object[][] puzzles = {
            {"Puzzle 1 -Easy", 
            "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4", 
            3},
            {"Puzzle 2 - Normal", 
            "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/3P1N2/PPP2PPP/RNBQK2R w KQkq - 0 5", 
            5},
            {"Puzzle 3 - Hard", 
            "r2qkb1r/pp2nppp/3p4/2pNN1B1/2BnP3/3P4/PPP2PPP/R2bK2R w KQkq - 1 8", 
            7},
            {"Puzzle 4 - Very Hard",
            "r1bq1rk1/pp2bppp/2n1pn2/3p4/2PP4/2NBPN2/PP3PPP/R1BQ1RK1 w - - 0 9",
            8}
        };
        
        String[] options = new String[puzzles.length];
        for (int i = 0; i < puzzles.length; i++) {
            options[i] = puzzles[i][0] + " (" + puzzles[i][2] + " moves)";
        }
        
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Choose a Puzzle to Solve:",
            "Choose Puzzle",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != null) {
            // Tìm puzzle được chọn
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(choice)) {
                    selectedPuzzleFEN = (String) puzzles[i][1];
                    selectedPuzzleMaxMoves = (Integer) puzzles[i][2];
                    // logger.info("Selected puzzle: {}, FEN: {}, MaxMoves: {}", 
                    //         puzzles[i][0], selectedPuzzleFEN, selectedPuzzleMaxMoves);
                    break;
                }
            }
            dispose();
        } else {
            // User cancelled, reset to default
            selectedMode = GameMode.PLAYER_VS_PLAYER;
        }
    }

    // Getters cho puzzle info
    public String getSelectedPuzzleFEN() {
        return selectedPuzzleFEN;
    }

    public int getSelectedPuzzleMaxMoves() {
        return selectedPuzzleMaxMoves;
    }

    /**
     * Shows a dialog for selecting the player's color in Player vs. AI mode.
     */
    private void showColorSelectionDialog() {
        ColorSelectionDialog colorDialog = new ColorSelectionDialog(null);
        colorDialog.setVisible(true);
        selectedColor = colorDialog.getSelectedColor();
        dispose();
    }

    /**
     * Gets the selected game mode.
     *
     * @return the selected game mode
     */
    public int getSelectedMode() {
        return selectedMode;
    }

    /**
     * Gets the selected color for the human player in Player vs. AI mode.
     *
     * @return the selected {@code PieceColor}
     */
    public PieceColor getSelectedColor() {
        return selectedColor;
    }

    /**
     * Handles the dialog close event, disposing the dialog and exiting the application.
     */
    public void onExit() {
        dispose();
        System.exit(0);
    }
}
