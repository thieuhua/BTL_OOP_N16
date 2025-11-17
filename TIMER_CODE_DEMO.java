/**
 * DEMO: Cách sử dụng ChessTimer
 * 
 * File này chỉ để minh họa, KHÔNG cần chạy!
 * Đây là code đã được integrate vào ChessController.java
 */

// ============================================
// 1. KHỞI TẠO TIMER
// ============================================

// Trong ChessController.java, method setPlayerVsPlayer()
public void setPlayerVsPlayer() {
    this.gameMode = GameMode.PLAYER_VS_PLAYER;
    this.whitePlayer = new HumanPlayer(this, PieceColor.WHITE);
    this.blackPlayer = new HumanPlayer(this, PieceColor.BLACK);
    
    // Khởi tạo timer với 5 phút cho mỗi người
    initializeTimer(5); // ← ĐÂY LÀ DÒNG QUAN TRỌNG!
    
    notifyGameStateChanged();
}

private void initializeTimer(int minutes) {
    // Tạo timer mới
    this.chessTimer = new ChessTimer(minutes);
    this.timerEnabled = true;
    
    // Đăng ký listener để nhận updates
    chessTimer.addTimerListener(new ChessTimer.TimerListener() {
        @Override
        public void onTimeUpdate(PieceColor color, long timeRemaining) {
            // Gọi khi timer update (mỗi 100ms)
            notifyTimerUpdate(color, timeRemaining);
        }

        @Override
        public void onTimeOut(PieceColor color) {
            // Gọi khi hết giờ
            handleTimeOut(color);
        }
    });
    
    // Start timer cho quân Trắng ngay lập tức
    chessTimer.startTimer(PieceColor.WHITE);
}

// ============================================
// 2. CHUYỂN TIMER KHI ĐỔI LƯỢT
// ============================================

// Trong method notifyTurnChanged()
void notifyTurnChanged() {
    for (PlayerPanelListener listener : playerPanelListeners) {
        listener.onTurnChanged(boardManager.getCurrentBoardState().getCurrentPlayerColor());
    }
    // Tự động chuyển timer khi đổi lượt
    switchTimer(); // ← DÒNG NÀY TỰ ĐỘNG CHUYỂN TIMER!
}

private void switchTimer() {
    if (timerEnabled && chessTimer != null) {
        PieceColor currentPlayer = boardManager.getCurrentBoardState().getCurrentPlayerColor();
        chessTimer.startTimer(currentPlayer); // Bên trong sẽ tự dừng timer bên kia
    }
}

// ============================================
// 3. XỬ LÝ KHI HẾT GIỜ
// ============================================

private void handleTimeOut(PieceColor color) {
    if (gameEnded) {
        return; // Đã kết thúc rồi, không xử lý nữa
    }
    
    gameEnded = true;
    String winner = color.isWhite() ? "Black" : "White";
    
    // Hiển thị dialog
    SwingUtilities.invokeLater(() -> {
        GameOverDialog dialog = new GameOverDialog(frame, winner + " wins by timeout!");
        dialog.setVisible(true);
    });
}

// ============================================
// 4. PAUSE/RESUME KHI UNDO/REDO
// ============================================

// Trong GameActionManager.java
public void undoMove() {
    if (controller.isGameEnded() || historyManager.getUndoStack().isEmpty()) {
        return;
    }

    controller.pauseTimer();      // ← Pause trước khi undo
    BoardState previousState = historyManager.getUndoStack().pop();
    restoreBoardState(previousState, true);
    controller.notifyHistoryChangeListeners();
    controller.resumeTimer();     // ← Resume sau khi undo xong
}

// ============================================
// 5. CẬP NHẬT UI (PlayerPanel)
// ============================================

// Trong PlayerPanel.java
@Override
public void onTimerUpdate(PieceColor color, long timeRemaining) {
    if (color == this.pieceColor) {
        // Chỉ update nếu là panel của màu này
        SwingUtilities.invokeLater(() -> updateTimer(timeRemaining));
    }
}

public void updateTimer(long timeMs) {
    // Format thời gian
    String timeText = ChessTimer.formatTime(timeMs); // "05:00"
    timerLabel.setText(timeText);
    
    // Đổi màu nếu sắp hết giờ
    boolean newTimeLow = timeMs < 30000; // < 30 giây
    if (newTimeLow != timeLow) {
        timeLow = newTimeLow;
        if (timeLow) {
            timerLabel.setForeground(new Color(255, 50, 50)); // ĐỎ!
        } else {
            timerLabel.setForeground(Color.WHITE); // Trắng
        }
    }
}

// ============================================
// 6. LOGIC BÊN TRONG ChessTimer.java
// ============================================

public class ChessTimer {
    private long whiteTimeRemaining; // milliseconds
    private long blackTimeRemaining; // milliseconds
    
    private Timer whiteTimer; // javax.swing.Timer
    private Timer blackTimer;
    
    public ChessTimer(int initialMinutes) {
        this.whiteTimeRemaining = initialMinutes * 60 * 1000L;
        this.blackTimeRemaining = initialMinutes * 60 * 1000L;
        initializeTimers();
    }
    
    private void initializeTimers() {
        // Timer update mỗi 100ms
        whiteTimer = new Timer(100, e -> {
            if (isWhiteTimerRunning) {
                whiteTimeRemaining -= 100; // Giảm 100ms
                notifyTimeUpdate(PieceColor.WHITE, whiteTimeRemaining);
                
                if (whiteTimeRemaining <= 0) {
                    whiteTimeRemaining = 0;
                    stopTimer(PieceColor.WHITE);
                    notifyTimeOut(PieceColor.WHITE); // HẾT GIỜ!
                }
            }
        });
        
        // Tương tự cho blackTimer...
    }
    
    public void startTimer(PieceColor color) {
        if (color.isWhite()) {
            if (!whiteTimer.isRunning()) {
                whiteTimer.start();
            }
            isWhiteTimerRunning = true;
            stopTimer(PieceColor.BLACK); // Dừng timer bên kia
        } else {
            if (!blackTimer.isRunning()) {
                blackTimer.start();
            }
            isBlackTimerRunning = true;
            stopTimer(PieceColor.WHITE); // Dừng timer bên kia
        }
    }
    
    public static String formatTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
        // Ví dụ: 125000ms → "02:05"
    }
}

// ============================================
// 7. FLOW HOÀN CHỈNH
// ============================================

/**
 * TIMELINE:
 * 
 * t=0s:    Game start → initializeTimer(5)
 *          → chessTimer.startTimer(WHITE)
 *          → White timer: 05:00, Black timer: 05:00
 * 
 * t=0.1s:  whiteTimer callback → whiteTimeRemaining -= 100
 *          → notifyTimeUpdate(WHITE, 04:59.9)
 *          → PlayerPanel updates UI → "04:59"
 * 
 * t=0.2s:  whiteTimer callback → "04:59"
 * ...
 * 
 * t=10s:   Player White di nuoc e2→e4
 *          → notifyTurnChanged()
 *          → switchTimer()
 *          → chessTimer.startTimer(BLACK)
 *          → White timer dừng ở 04:50
 *          → Black timer bắt đầu đếm
 * 
 * t=20s:   Player Black di nuoc e7→e5
 *          → switchTimer()
 *          → Black timer dừng
 *          → White timer tiếp tục từ 04:50
 * 
 * ...
 * 
 * t=4:30:  White timer: 00:29 → timeLow = true
 *          → Timer chuyển màu ĐỎ!
 * 
 * t=5:00:  White timer: 00:00
 *          → onTimeOut(WHITE)
 *          → handleTimeOut(WHITE)
 *          → Dialog: "Black wins by timeout!"
 *          → Game over
 */

// ============================================
// 8. CUSTOMIZE EXAMPLES
// ============================================

// Example 1: Đổi thời gian thành 3 phút (Blitz)
initializeTimer(3);

// Example 2: Đổi thời gian thành 1 phút (Bullet)
initializeTimer(1);

// Example 3: Đổi ngưỡng cảnh báo thành 10 giây
boolean newTimeLow = timeMs < 10000; // 10 giây

// Example 4: Update nhanh hơn (mỗi 50ms)
whiteTimer = new Timer(50, e -> { ... });

// Example 5: Thêm increment time (Fischer clock)
public void startTimer(PieceColor color, long incrementMs) {
    if (color.isWhite()) {
        whiteTimeRemaining += incrementMs; // Thêm thời gian
        isWhiteTimerRunning = true;
    }
}

// Example 6: Âm thanh cảnh báo
if (timeRemaining <= 10000 && timeRemaining > 9900) {
    SoundPlayer.playTickTockSound();
}

// ============================================
// 9. DEBUG TIPS
// ============================================

// Tip 1: Log mọi timer update
private void notifyTimeUpdate(PieceColor color, long timeRemaining) {
    System.out.println(color + " timer: " + formatTime(timeRemaining));
    // ...
}

// Tip 2: Test nhanh với thời gian giảm nhanh
whiteTimeRemaining -= 5000; // Giảm 5 giây mỗi lần thay vì 0.1 giây

// Tip 3: Breakpoint để debug
public void startTimer(PieceColor color) {
    // Đặt breakpoint ở đây để xem timer state
    if (color.isWhite()) {
        ...
    }
}

// ============================================
// 10. COMMON MISTAKES & SOLUTIONS
// ============================================

// ❌ SAI: Update UI không dùng invokeLater
private void notifyTimeUpdate(PieceColor color, long timeRemaining) {
    for (TimerListener listener : listeners) {
        listener.onTimeUpdate(color, timeRemaining); // NOT THREAD-SAFE!
    }
}

// ✅ ĐÚNG: Luôn dùng invokeLater cho UI
private void notifyTimeUpdate(PieceColor color, long timeRemaining) {
    SwingUtilities.invokeLater(() -> {
        for (TimerListener listener : listeners) {
            listener.onTimeUpdate(color, timeRemaining); // THREAD-SAFE!
        }
    });
}

// ❌ SAI: Quên dừng timer bên kia
public void startTimer(PieceColor color) {
    if (color.isWhite()) {
        whiteTimer.start();
        // Quên dừng blackTimer!
    }
}

// ✅ ĐÚNG: Luôn dừng timer bên kia
public void startTimer(PieceColor color) {
    if (color.isWhite()) {
        whiteTimer.start();
        stopTimer(PieceColor.BLACK); // Dừng timer bên kia!
    }
}

// ❌ SAI: Không kiểm tra gameEnded
private void handleTimeOut(PieceColor color) {
    gameEnded = true; // Set trước
    // Nếu timeout 2 lần cùng lúc sẽ lỗi!
}

// ✅ ĐÚNG: Kiểm tra trước
private void handleTimeOut(PieceColor color) {
    if (gameEnded) {
        return; // Đã kết thúc rồi
    }
    gameEnded = true;
    // ...
}

// ============================================
// END OF DEMO
// ============================================

/**
 * TÓM TẮT:
 * 
 * 1. ChessTimer: Quản lý 2 timer (White & Black)
 * 2. ChessController: Khởi tạo và điều phối timer
 * 3. PlayerPanel: Hiển thị UI timer
 * 4. Observer Pattern: Timer → Controller → Panel
 * 5. Thread-Safe: Dùng SwingUtilities.invokeLater()
 * 6. Auto-switch: Timer tự chuyển khi đổi lượt
 * 7. Timeout: Tự động kết thúc game khi hết giờ
 * 
 * DONE! 🎉
 */

