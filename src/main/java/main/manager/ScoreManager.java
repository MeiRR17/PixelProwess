package main.manager;

import main.menu.GamePanel;
import main.menu.GameState;
import game.object.mob.Goblin;
import game.object.mob.Orc;
import game.object.mob.Satan;
import game.object.mob.Uruk;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ScoreManager {
    private final GamePanel gamePanel;
    private final GameState gameState;
    private BufferedImage scorePanel;
    private int currentScore;
    private int mobSpawnTimer;
    private final int MOB_SPAWN_INTERVAL = 600;
    private final int SCORE_PANEL_WIDTH = 200;
    private final int SCORE_PANEL_HEIGHT = 100;

    public ScoreManager(GamePanel gamePanel, GameState gameState) throws IOException {
        this.gamePanel = gamePanel;
        this.gameState = gameState;
        this.currentScore = 0;
        this.mobSpawnTimer = 0;
        loadScorePanel();
    }

    private void loadScorePanel() throws IOException {
        scorePanel = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("UI/points panel1.png")));
    }

    public void update() {
        //update mob spawn timer
        mobSpawnTimer++;
        if(mobSpawnTimer >= MOB_SPAWN_INTERVAL) {
            spawnNewMobs();
            mobSpawnTimer = 0;
        }
    }

    private void spawnNewMobs() {
        //Calculate how many mobs to spawn based on current score
        int baseSpawnCount = 1 + (currentScore / 1000);

        for(int i = 0; i < baseSpawnCount; i++) {
            double random = Math.random();

            //spawn chances
            if(random < 0.4) {
                gamePanel.mobs.add(new Goblin(gamePanel, gamePanel.player, gamePanel.map, gameState.getPlayerLevel()));
            } else if(random < 0.7) {
                gamePanel.mobs.add(new Orc(gamePanel, gamePanel.player, gamePanel.map, gameState.getPlayerLevel()));
            } else if(random < 0.9) {
                gamePanel.mobs.add(new Uruk(gamePanel, gamePanel.player, gamePanel.map, gameState.getPlayerLevel()));
            } else {
                gamePanel.mobs.add(new Satan(gamePanel, gamePanel.player, gamePanel.map, gameState.getPlayerLevel()));
            }
        }
    }

    public void addScore(int points) {
        currentScore += points;

        // Fetch the current high score from the game state
        int currentHighScore = gameState.getHighScore();

        if (currentScore > currentHighScore) {
            System.out.println("Updating score for user: " + gameState.getCurrentUserId());
            gameState.saveHighScore(gameState.getCurrentUserId(), currentScore);
        } else {
            System.out.println("Score not updated. Current high score: " + currentHighScore + ", New score: " + currentScore);
        }
    }

    public void draw(Graphics2D g) {
        // Draw score panel at top right of the screen
        int panelX = gamePanel.screenWidth - SCORE_PANEL_WIDTH;
        int panelY = 0;

        g.drawImage(scorePanel, panelX, panelY, SCORE_PANEL_WIDTH, SCORE_PANEL_HEIGHT, null);

        // Set up text properties
        g.setColor(Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();

        // Calculate base coordinates relative to panel
        int baseX = panelX + 69; // Starting X coordinate
        int boxWidth = 112 - 69; // Width of text area

        // Current score
        String currentScoreText = String.valueOf(currentScore);
        int currentScoreWidth = metrics.stringWidth(currentScoreText);
        int currentScoreX = baseX + (boxWidth / 2) + (currentScoreWidth / 2);
        // Adjust Y coordinate to be lower in the box
        int currentScoreY = panelY + 43;

        // High score
        String highScoreText = String.valueOf(gameState.getHighScore());
        int highScoreWidth = metrics.stringWidth(highScoreText);
        int highScoreX = baseX + (boxWidth / 2) + (highScoreWidth / 2);
        // Adjust Y coordinate to be lower in the box
        int highScoreY = panelY + 70;

        // Draw scores with slight adjustments
        g.drawString(currentScoreText, currentScoreX, currentScoreY);
        g.drawString(highScoreText, highScoreX, highScoreY);
    }
    public int getScore() {
        return getCurrentScore();
    }
    public void resetScore() {
        currentScore = 0;
        mobSpawnTimer = 0;
    }
    public int getCurrentScore() {
        return currentScore;
    }
}
