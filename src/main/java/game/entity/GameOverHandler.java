package game.entity;

import game.object.mob.Mob;
import main.menu.GamePanel;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class GameOverHandler {
    private GamePanel gamePanel;
    private Player player;
    private boolean isGameOver = false;
    private int floatYOffset = 0;
    private final int FLOAT_SPEED = 8; // Increased from 2 to 4 for faster animation
    public final int MAX_FLOAT_HEIGHT = 100;
    private int mobKillIndex = 0;
    private Timer mobKillTimer;
    private float fadeAlpha = 1.0f;
    private boolean transitioningToMenu = false;
    private final int TOTAL_SEQUENCE_TIME = 2500; // Reduced from 3000 to 2500 ms (2.5 seconds)
    private long gameOverStartTime;
    private boolean hasCompletedSequence = false;

    public GameOverHandler(GamePanel gamePanel, Player player) {
        this.gamePanel = gamePanel;
        this.player = player;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getFloatYOffset() {
        return floatYOffset;
    }

    public void checkGameOver() {
        if (!isGameOver && player.health <= 0) {
            startGameOver();
        }
    }

    private void startGameOver() {
        System.out.println("Game Over sequence started!");
        isGameOver = true;
        player.setDying(true);
        gameOverStartTime = System.currentTimeMillis();

        // Force a shorter delay before starting mob killing sequence
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Starting mob kill sequence");
                startMobKillSequence();
            }
        }, 700); // Reduced from 1000 to 700 ms for faster player animation
    }

    private void startMobKillSequence() {
        int totalMobs = gamePanel.mobs.size();
        System.out.println("Killing " + totalMobs + " mobs");

        if (totalMobs == 0) {
            // No mobs to kill, proceed to fade out
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startFadeOut();
                }
            }, 300); // Reduced from 500 to 300 ms
            return;
        }

        // Kill mobs at a faster rate to complete in about 1 second instead of 1.5
        final int killInterval = 1000 / Math.max(1, totalMobs);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mobKillIndex < totalMobs) {
                    Mob currentMob = gamePanel.mobs.get(mobKillIndex);
                    currentMob.takeDamage(currentMob.getHealth());
                    mobKillIndex++;
                    System.out.println("Killed mob " + mobKillIndex + " of " + totalMobs);
                } else {
                    this.cancel();
                    // Ensure we wait for the last mob's death animation to complete
                    // before starting the fade out
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startFadeOut();
                        }
                    }, 400); // Slightly reduced from 500 to 400 ms
                }
            }
        }, 0, Math.max(20, killInterval)); // Minimum interval reduced from 30 to 20 ms
    }

    private void startFadeOut() {
        if (transitioningToMenu) return;
        transitioningToMenu = true;
        System.out.println("Starting fade out");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fadeAlpha -= 0.07f; // Increased from 0.05f to 0.07f for faster fade
                if (fadeAlpha <= 0) {
                    fadeAlpha = 0;
                    this.cancel();

                    // Add a shorter final delay before returning to menu
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!hasCompletedSequence) {
                                hasCompletedSequence = true;
                                returnToPlayMenu();
                            }
                        }
                    }, 200); // Reduced from 300 to 200 ms
                }
            }
        }, 0, 40); // Fade over ~0.8 seconds instead of 1 second (reduced from 50 to 40 ms)
    }

    private void returnToPlayMenu() {
        System.out.println("Returning to play menu");

        // Save final score to GameState and update online
        final int finalScore = gamePanel.scoreManager.getScore();
        gamePanel.gameState.updateOnlineScore(finalScore); // Let GameState handle the logic

        // Use SwingUtilities to ensure UI updates happen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Stop the game thread ONLY after the sequence is complete
            gamePanel.stopGameThread();

            // Reset the internal state of the current game panel
            gamePanel.resetGame();

            // Show the play menu using the CardLayout
            JPanel cardPanel = (JPanel) gamePanel.getParent();
            if (cardPanel != null) {
                CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                cardLayout.show(cardPanel, "playMenu");
            }
        });
    }

    public void update() {
        if (isGameOver && player.isDying()) {
            // Update player float animation with increased speed
            if (floatYOffset < MAX_FLOAT_HEIGHT) {
                floatYOffset += FLOAT_SPEED;
            }

            // Make sure the player stays visible during float animation
            player.setVisible(true);
        }
    }

    public void draw(Graphics2D g2) {
        if (!isGameOver) return;

        // Draw a "Game Over" message
        if (fadeAlpha > 0.3f) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, gamePanel.screenWidth/2 - textWidth/2, gamePanel.screenHeight/2 - 24);
        }

        // Apply fade effect if active
        if (fadeAlpha < 1.0f) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));

            // Draw a black rectangle over the screen for fade effect
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);

            g2.setComposite(originalComposite);
        }
    }
}