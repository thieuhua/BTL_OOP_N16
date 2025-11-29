package nhom16oop.game;

import nhom16oop.constants.PieceColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages chess game timers for both players.
 * Handles countdown, pause, resume, and time-out events.
 */
public class ChessTimer {
    private static final Logger logger = LoggerFactory.getLogger(ChessTimer.class);

    private long whiteTimeRemaining; // in milliseconds
    private long blackTimeRemaining; // in milliseconds
    private final long initialTime; // in milliseconds

    private Timer whiteTimer;
    private Timer blackTimer;

    private boolean isWhiteTimerRunning;
    private boolean isBlackTimerRunning;

    private final List<TimerListener> listeners = new ArrayList<>();

    /**
     * Creates a new ChessTimer with specified initial time for each player.
     *
     * @param initialMinutes the initial time in minutes for each player
     */
    public ChessTimer(int initialMinutes) {
        this.initialTime = initialMinutes * 60 * 1000L;
        this.whiteTimeRemaining = initialTime;
        this.blackTimeRemaining = initialTime;
        this.isWhiteTimerRunning = false;
        this.isBlackTimerRunning = false;

        initializeTimers();
        logger.info("ChessTimer initialized with {} minutes per player", initialMinutes);
    }
    // public ChessTimer(long initialTime, long whiteTimeRemaining, long blackTimeRemaining) {
    //     this.initialTime = initialTime;
    //     this.isWhiteTimerRunning = false;
    //     this.isBlackTimerRunning = false;

    //     initializeTimers();
    //     logger.info("ChessTimer initialized with {} ms total, white: {} ms, black: {} ms", initialTime, whiteTimeRemaining, blackTimeRemaining);
    // }

    private void initializeTimers() {
        // Timer updates every 100ms for smooth display
        whiteTimer = new Timer(100, e -> {
            if (isWhiteTimerRunning) {
                whiteTimeRemaining -= 100;
                notifyTimeUpdate(PieceColor.WHITE, whiteTimeRemaining);

                if (whiteTimeRemaining <= 0) {
                    whiteTimeRemaining = 0;
                    stopTimer(PieceColor.WHITE);
                    notifyTimeOut(PieceColor.WHITE);
                    logger.info("White player ran out of time");
                }
            }
        });

        blackTimer = new Timer(100, e -> {
            if (isBlackTimerRunning) {
                blackTimeRemaining -= 100;
                notifyTimeUpdate(PieceColor.BLACK, blackTimeRemaining);

                if (blackTimeRemaining <= 0) {
                    blackTimeRemaining = 0;
                    stopTimer(PieceColor.BLACK);
                    notifyTimeOut(PieceColor.BLACK);
                    logger.info("Black player ran out of time");
                }
            }
        });
    }

    /**
     * Starts the timer for the specified color.
     *
     * @param color the color of the player whose timer should start
     */
    public void startTimer(PieceColor color) {
        if (color.isWhite()) {
            if (!whiteTimer.isRunning()) {
                whiteTimer.start();
            }
            isWhiteTimerRunning = true;
            stopTimer(PieceColor.BLACK);
            logger.debug("White timer started");
        } else {
            if (!blackTimer.isRunning()) {
                blackTimer.start();
            }
            isBlackTimerRunning = true;
            stopTimer(PieceColor.WHITE);
            logger.debug("Black timer started");
        }
    }

    /**
     * Stops the timer for the specified color.
     *
     * @param color the color of the player whose timer should stop
     */
    public void stopTimer(PieceColor color) {
        if (color.isWhite()) {
            isWhiteTimerRunning = false;
            logger.debug("White timer stopped");
        } else {
            isBlackTimerRunning = false;
            logger.debug("Black timer stopped");
        }
    }

    /**
     * Stops all timers.
     */
    public void stopAllTimers() {
        isWhiteTimerRunning = false;
        isBlackTimerRunning = false;
        if (whiteTimer != null) {
            whiteTimer.stop();
        }
        if (blackTimer != null) {
            blackTimer.stop();
        }
        logger.debug("All timers stopped");
    }

    /**
     * Resets both timers to initial time.
     */
    public void reset() {
        stopAllTimers();
        whiteTimeRemaining = initialTime;
        blackTimeRemaining = initialTime;
        notifyTimeUpdate(PieceColor.WHITE, whiteTimeRemaining);
        notifyTimeUpdate(PieceColor.BLACK, blackTimeRemaining);
        logger.info("Timers reset to initial time");
    }

    /**
     * Gets the remaining time for the specified color.
     *
     * @param color the color to get time for
     * @return remaining time in milliseconds
     */
    public long getTimeRemaining(PieceColor color) {
        return color.isWhite() ? whiteTimeRemaining : blackTimeRemaining;
    }
    public void setTimeRemaining(PieceColor color, long timeMs) {
        if (color.isWhite()) {
            this.whiteTimeRemaining = timeMs;
        } else {
            this.blackTimeRemaining = timeMs;
        }
    }

    /**
     * Formats time in milliseconds to MM:SS format.
     *
     * @param timeMs time in milliseconds
     * @return formatted time string
     */
    public static String formatTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Checks if a player's time is running low (less than 30 seconds).
     *
     * @param color the color to check
     * @return true if time is running low
     */
    public boolean isTimeLow(PieceColor color) {
        long timeRemaining = color.isWhite() ? whiteTimeRemaining : blackTimeRemaining;
        return timeRemaining < 30000; // less than 30 seconds
    }

    /**
     * Adds a timer listener.
     *
     * @param listener the listener to add
     */
    public void addTimerListener(TimerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a timer listener.
     *
     * @param listener the listener to remove
     */
    public void removeTimerListener(TimerListener listener) {
        listeners.remove(listener);
    }

    private void notifyTimeUpdate(PieceColor color, long timeRemaining) {
        SwingUtilities.invokeLater(() -> {
            for (TimerListener listener : listeners) {
                listener.onTimeUpdate(color, timeRemaining);
            }
        });
    }

    private void notifyTimeOut(PieceColor color) {
        SwingUtilities.invokeLater(() -> {
            for (TimerListener listener : listeners) {
                listener.onTimeOut(color);
            }
        });
    }

/**
 * Listener interface for timer events.
 */
public interface TimerListener {
        /**
         * Called when time is updated.
         *
         * @param color         the player's color
         * @param timeRemaining remaining time in milliseconds
         */
        void onTimeUpdate(PieceColor color, long timeRemaining);

        /**
         * Called when a player runs out of time.
         *
         * @param color the player's color who ran out of time
         */
        void onTimeOut(PieceColor color);
    }
}

