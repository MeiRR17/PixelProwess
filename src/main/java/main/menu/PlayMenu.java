package main.menu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import main.data.DatabasePlayerLoader;
import game.entity.CharacterStats;
import game.entity.Player;

public class PlayMenu extends JPanel implements MouseListener, MouseMotionListener {


    private boolean debug = false;


    // Original polygon coordinates at 1080p resolution
    private static final int[] PIP_PANEL_X = {842, 1346, 1366, 1366, 1263, 826, 825};
    private static final int[] PIP_PANEL_Y = {60, 60, 81, 266, 373, 372, 75};

    private static final int[] FINN_PANEL_X = {1375, 1908, 1908, 1479, 1375};
    private static final int[] FINN_PANEL_Y = {77, 77, 374, 373, 266};

    private static final int[] RILEY_PANEL_X = {826, 1263, 1365, 1365, 847, 824};
    private static final int[] RILEY_PANEL_Y = {381, 382, 482, 693, 699, 668};

    private static final int[] BROCK_PANEL_X = {1478, 1908, 1908, 1885, 1374, 1375};
    private static final int[] BROCK_PANEL_Y = {383, 384, 676, 699, 693, 482};

    private static final int[] READY_BUTTON_X = {1578, 1676, 1918, 1920};
    private static final int[] READY_BUTTON_Y = {1079, 982, 982, 1080};

    private static int[] SHUFFLE_PANEL_X = {1262, 1480, 1480, 1262};
    private static int[] SHUFFLE_PANEL_Y = {266, 266, 482, 482};

    private static final int[] SELECTED_CHARACTER_PANEL_X = {846, 1074, 1074, 846};
    private static final int[] SELECTED_CHARACTER_PANEL_Y = {733, 733, 967, 967};

    private static final int[] BACK_BUTTON_X = {0, 206, 137, 0};
    private static final int[] BACK_BUTTON_Y = {0, 0, 75, 75};
    private Polygon backButton;

    // Define circle parameters for shuffle panel (center point and radius)
    private static final int SHUFFLE_CENTER_X = 1371;  // Average of original x coordinates
    private static final int SHUFFLE_CENTER_Y = 374;   // Average of original y coordinates
    private static final int SHUFFLE_RADIUS = 107;     // Adjust this to match your UI
    private static final int CIRCLE_POINTS = 30;       // Number of points to create circle





    // Screen scaling
    private final double REFERENCE_WIDTH = 1920.0;
    private final double REFERENCE_HEIGHT = 1080.0;
    private double scaleX, scaleY;
    private final GameState gameState;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Get screen dimensions directly
    private final int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int screenHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30);

    // UI Components
    private BufferedImage backgroundImage;
    private BufferedImage pipImage, finnImage, rileyImage, brockImage, shuffleImage;
    private CharacterStats currentCharacterStats;
    private DatabasePlayerLoader dbLoader;

    // Character selection panels as polygons
    private Polygon pipPanel, finnPanel, rileyPanel, brockPanel;
    private Polygon readyButton, shufflePanel, selectedCharacterPanel;

    private String selectedCharacter = null;
    private boolean isHovering = false;
    private String hoveringCharacter = null;

    // Font settings
    private Font statsFont;
    private final int STAT_FONT_SIZE = 22;

    public PlayMenu(GameState gameState, CardLayout cardLayout, JPanel cardPanel) {
        this.gameState = gameState;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.dbLoader = gameState.getDbLoader();

        setPreferredSize(new Dimension(screenWidth, screenHeight));

        // Calculate scaling factors
        scaleX = (double) screenWidth / REFERENCE_WIDTH;
        scaleY = (double) screenHeight / REFERENCE_HEIGHT;

        // Initialize font with scaled size
        statsFont = new Font("Arial", Font.BOLD, (int)(STAT_FONT_SIZE * Math.min(scaleX, scaleY)));

        // Initialize scaled polygons
        initializePolygons();

        loadImages();
        initializeShufflePanel();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void drawCharacterPreview(Graphics2D g2) {
        if (selectedCharacter != null || (isHovering && hoveringCharacter != null)) {
            BufferedImage characterImage = null;
            String displayCharacter = selectedCharacter != null ? selectedCharacter : hoveringCharacter;

            // Get character image
            switch (displayCharacter.toLowerCase()) {
                case "pip" -> characterImage = pipImage;
                case "finn" -> characterImage = finnImage;
                case "riley" -> characterImage = rileyImage;
                case "brock" -> characterImage = brockImage;
                case "shuffle" -> characterImage = shuffleImage;
            }

            if (characterImage != null) {
                // Calculate the preview panel dimensions
                Rectangle bounds = selectedCharacterPanel.getBounds();
                int panelWidth = bounds.width;
                int panelHeight = bounds.height;

                // Calculate scaling to maintain aspect ratio but fit height
                double scale = (double) panelHeight / characterImage.getHeight();
                int scaledWidth = (int) (characterImage.getWidth() * scale);
                int scaledHeight = panelHeight;

                // Center horizontally in the panel
                int x = bounds.x + (panelWidth - scaledWidth) / 2;
                int y = bounds.y;

                g2.drawImage(characterImage, x, y, scaledWidth, scaledHeight, null);
            }
        }
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/game set/play/play menu.png")));
            pipImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/pip/down/stand.png")));
            finnImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/finn/down/stand.png")));
            rileyImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/riley/down/stand.png")));
            brockImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/brock/down/stand.png")));
            shuffleImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/game set/play/shuffle.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Enable anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        // Draw character preview
        drawCharacterPreview(g2);

        // Draw stats if character is selected
        if (selectedCharacter != null) {
            drawPlayerStats(g2);
            drawMobStats(g2);
        }

        // Draw high score
        drawHighScore(g2);

        // Draw debug visualization if enabled
        if (debug) {
            drawDebugVisualization(g2);
        }
    }

    private void drawHighScore(Graphics2D g2) {
        String currentUserId = gameState.getCurrentUserId();

        // Only check if user is logged in, don't require character selection
        if (currentUserId != null) {
            // Get high score directly from GameState
            int highScore;
            if (selectedCharacter != null) {
                // Try to get character-specific high score if that feature exists
                // Otherwise use overall high score
                highScore = gameState.getHighScore();
            } else {
                // Use overall high score when no character is selected
                highScore = gameState.getHighScore();
            }
            // Set up font and color for high score
            g2.setFont(statsFont);
            g2.setColor(Color.WHITE);

            // Calculate text metrics for centering
            FontMetrics metrics = g2.getFontMetrics(statsFont);
            String scoreText = String.valueOf(highScore);
            int textWidth = metrics.stringWidth(scoreText);

            // Apply screen scaling to box boundaries
            int boxX = (int)(282 * scaleX);
            int boxY = (int)(1011 * scaleY);
            int boxWidth = (int)((505 - 282) * scaleX);
            int boxHeight = (int)((1068 - 1011) * scaleY);

            // Center the text
            int x = boxX + (boxWidth / 2) - (textWidth / 2);
            int y = boxY + (boxHeight * 2/3); // Move text lower in the box

            g2.drawString(scoreText, x, y);
        }
    }
    private void drawDebugVisualization(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2));

        // Draw all panels except shuffle
        drawDebugPanel(g2, pipPanel, "Pip Panel", new Color(255, 0, 0, 128));
        drawDebugPanel(g2, finnPanel, "Finn Panel", new Color(0, 0, 255, 128));
        drawDebugPanel(g2, rileyPanel, "Riley Panel", new Color(0, 255, 0, 128));
        drawDebugPanel(g2, brockPanel, "Brock Panel", new Color(255, 255, 0, 128));
        drawDebugPanel(g2, readyButton, "Ready Button", new Color(255, 0, 255, 128));
        drawDebugPanel(g2, selectedCharacterPanel, "Character Preview", new Color(255, 255, 255, 128));
        drawDebugPanel(g2, backButton, "Back Button", new Color(128, 0, 255, 128));

        // Draw shuffle panel last (on top)
        drawDebugPanel(g2, shufflePanel, "Shuffle Panel", new Color(0, 255, 255, 128));
    }

    private void drawDebugPanel(Graphics2D g2, Polygon polygon, String label, Color color) {
        g2.setColor(color);
        g2.drawPolygon(polygon);

        // Draw label
        Rectangle bounds = polygon.getBounds();
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label,
                bounds.x + (bounds.width - fm.stringWidth(label)) / 2,
                bounds.y + bounds.height / 2);
    }

    private void initializeShufflePanel() {
        int[] xPoints = new int[CIRCLE_POINTS];
        int[] yPoints = new int[CIRCLE_POINTS];

        for (int i = 0; i < CIRCLE_POINTS; i++) {
            double angle = 2 * Math.PI * i / CIRCLE_POINTS;
            xPoints[i] = SHUFFLE_CENTER_X + (int)(SHUFFLE_RADIUS * Math.cos(angle));
            yPoints[i] = SHUFFLE_CENTER_Y + (int)(SHUFFLE_RADIUS * Math.sin(angle));
        }

        SHUFFLE_PANEL_X = xPoints;
        SHUFFLE_PANEL_Y = yPoints;
    }
    // Method to toggle debug mode
    public void toggleDebug() {
        debug = !debug;
        repaint();
    }

    private void drawPlayerStats(Graphics2D g2) {
        g2.setFont(statsFont);
        g2.setColor(Color.WHITE);

        if (selectedCharacter != null && !selectedCharacter.equals("shuffle")) {
            CharacterStats stats = getCurrentCharacterStats();
            if (stats == null) return;

            // Calculate final stats
            int finalHealth = (int)(100 * stats.getHealthMultiplier());
            int finalSpeed = (int)(Player.SPEED * stats.getSpeedMultiplier());
            double finalCritChance = 0.05 * stats.getCriticalHitChanceMultiplier() * 100;

            // Draw stats with scaled positions
            drawScaledText(g2, " " + stats.getLevel(), 1316, 807);
            drawScaledText(g2, " +" + String.format("%.0f%%", (stats.getAttackMultiplier() - 1) * 100), 1352, 863);
            drawScaledText(g2, " " + String.format("%.1f%%", finalCritChance), 1298, 919);
            drawScaledText(g2, " " + finalHealth, 1715, 807);
            drawScaledText(g2, " +" + String.format("%.0f%%", (stats.getDefenseMultiplier() - 1) * 100), 1732, 863);
            drawScaledText(g2, " " + finalSpeed, 1688, 919);
        }
    }

    private void drawMobStats(Graphics2D g2) {
        g2.setFont(statsFont);
        g2.setColor(Color.WHITE);

        double scaleX = (double) screenWidth / 1920;
        double scaleY = (double) screenHeight / 1080;

        if (selectedCharacter != null && !selectedCharacter.equals("shuffle")) {
            CharacterStats stats = getCurrentCharacterStats();
            if (stats == null) return;

            int playerLevel = stats.getLevel();
            String levelRange = (playerLevel) + "-" + (playerLevel + 10);
            double expBonus = (playerLevel - 1) * 6.0;
            double statBonus = (playerLevel - 1) * 6.0;

            drawScaledText(g2, levelRange, 224, 833, scaleX, scaleY);
            drawScaledText(g2, " +" + formatDouble(expBonus) + "%", 191, 889, scaleX, scaleY);
            drawScaledText(g2, " +" + formatDouble(statBonus) + "%", 610, 805, scaleX, scaleY);
            drawScaledText(g2, " +" + formatDouble(statBonus) + "%", 629, 861, scaleX, scaleY);
            drawScaledText(g2, " +" + formatDouble(statBonus) + "%", 584, 917, scaleX, scaleY);
        } else if (selectedCharacter != null && selectedCharacter.equals("shuffle")) {
            // Only show level for shuffle
            drawScaledText(g2, " ?", 224, 833, scaleX, scaleY);
            drawScaledText(g2, " ?", 1316, 807, scaleX, scaleY);
        }
    }
    private void drawScaledText(Graphics2D g2, String text, int x, int y, double scaleX, double scaleY) {
        g2.drawString(text, (int)(x * scaleX), (int)(y * scaleY));
    }
    private void drawScaledText(Graphics2D g2, String text, int x, int y) {
        g2.drawString(text, (int)(x * scaleX), (int)(y * scaleY));
    }

    private CharacterStats getCurrentCharacterStats() {
        if (selectedCharacter == null || selectedCharacter.equals("shuffle")) return null;

        // Use preloaded stats if available
        CharacterStats preloadedStats = gameState.getPreloadedStats(selectedCharacter);
        if (preloadedStats != null) {
            return preloadedStats;
        }

        // Fallback to creating new stats if preloaded ones aren't ready
        if (currentCharacterStats == null ||
                !currentCharacterStats.getCharacterName().equals(selectedCharacter)) {
            currentCharacterStats = new CharacterStats(selectedCharacter, dbLoader);
        }

        return currentCharacterStats;
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        // Character selection
        if (shufflePanel.contains(p)) {
            // Create array of available characters
            String[] characters = {"pip", "finn", "riley", "brock"};
            // random index
            int randomIndex = (int) (Math.random() * characters.length);
            // Set selected character to random choice
            selectedCharacter = characters[randomIndex];
            // Update character stats
            updateCharacterStats(selectedCharacter);
            // Update the GameState
            gameState.setSelectedCharacter(selectedCharacter);
        } else if (pipPanel.contains(p)) {
            selectedCharacter = "pip";
            updateCharacterStats("pip");
            gameState.setSelectedCharacter(selectedCharacter);
        } else if (finnPanel.contains(p)) {
            selectedCharacter = "finn";
            updateCharacterStats("finn");
            gameState.setSelectedCharacter(selectedCharacter);
        } else if (rileyPanel.contains(p)) {
            selectedCharacter = "riley";
            updateCharacterStats("riley");
            gameState.setSelectedCharacter(selectedCharacter);
        } else if (brockPanel.contains(p)) {
            selectedCharacter = "brock";
            updateCharacterStats("brock");
            gameState.setSelectedCharacter(selectedCharacter);
        } else if (readyButton.contains(p)) {
            // Ready button logic
            if (selectedCharacter == null) {
                // Show a message if no character is selected
                JOptionPane.showMessageDialog(this,
                        "Please select a character first!",
                        "No Character Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Restart the game with a fresh state
                gameState.restartGame();

                // Get the fresh game panel
                GamePanel gamePanel = gameState.getGamePanel();

                // Add it to the card panel
                cardPanel.add(gamePanel, "Game");

                // Switch to game panel
                cardLayout.show(cardPanel, "Game");

                // Request focus and start the game thread
                gamePanel.requestFocus();
                gamePanel.startGameThread();

                // Start the background music
                gameState.getSoundManager().setMusicEnabled(true);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (backButton.contains(p)) {
            cardLayout.show(cardPanel, "MainMenu");
        }

        repaint();
    }

    // Add this method if it doesn't exist already
    private void updateCharacterStats(String character) {
        currentCharacterStats = gameState.getPreloadedStats(character);
    }



    private void initializePolygons() {
        pipPanel = createScaledPolygon(PIP_PANEL_X, PIP_PANEL_Y);
        finnPanel = createScaledPolygon(FINN_PANEL_X, FINN_PANEL_Y);
        rileyPanel = createScaledPolygon(RILEY_PANEL_X, RILEY_PANEL_Y);
        brockPanel = createScaledPolygon(BROCK_PANEL_X, BROCK_PANEL_Y);
        readyButton = createScaledPolygon(READY_BUTTON_X, READY_BUTTON_Y);
        shufflePanel = createScaledPolygon(SHUFFLE_PANEL_X, SHUFFLE_PANEL_Y);
        selectedCharacterPanel = createScaledPolygon(SELECTED_CHARACTER_PANEL_X, SELECTED_CHARACTER_PANEL_Y);
        backButton = createScaledPolygon(BACK_BUTTON_X, BACK_BUTTON_Y);
    }

    private Polygon createScaledPolygon(int[] xPoints, int[] yPoints) {
        int[] scaledX = new int[xPoints.length];
        int[] scaledY = new int[yPoints.length];

        for (int i = 0; i < xPoints.length; i++) {
            scaledX[i] = (int)(xPoints[i] * scaleX);
            scaledY[i] = (int)(yPoints[i] * scaleY);
        }

        return new Polygon(scaledX, scaledY, xPoints.length);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        isHovering = true;

        if (pipPanel.contains(p)) hoveringCharacter = "pip";
        else if (finnPanel.contains(p)) hoveringCharacter = "finn";
        else if (rileyPanel.contains(p)) hoveringCharacter = "riley";
        else if (brockPanel.contains(p)) hoveringCharacter = "brock";
        else if (shufflePanel.contains(p)) hoveringCharacter = "shuffle";
        else {
            isHovering = false;
            hoveringCharacter = null;
        }

        repaint();
    }

    private void startGame() {
        gameState.setSelectedCharacter(selectedCharacter);
        try {
            gameState.getSoundManager().switchToGameMusic();
            // Set the selected character in GameState
            gameState.setSelectedCharacter(selectedCharacter);

            // Initialize GamePanel
            gameState.initializeGamePanel();
            GamePanel gamePanel = gameState.getGamePanel();
            if (gamePanel.player != null && gamePanel.player.gameOverHandler != null &&
                    gamePanel.player.gameOverHandler.isGameOver()) {

                gamePanel.resetGame();
            }
            // Set up the game
            gamePanel.gameSet();
            gamePanel.startGameThread();

            // Add game panel to card panel and switch to it
            cardPanel.add(gamePanel, "GamePanel");
            cardLayout.show(cardPanel, "GamePanel");
            gamePanel.requestFocus();

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error starting game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public void reset() {
        // Reset character selection
        selectedCharacter = null;
        hoveringCharacter = null;
        isHovering = false;

        // Make sure the UI is ready for a new game
        repaint();
    }
    private String formatDouble(double value) {
        return String.format("%.1f", value);
    }
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        isHovering = false;
        hoveringCharacter = null;
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}
}