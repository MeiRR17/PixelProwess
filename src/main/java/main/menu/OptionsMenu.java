package main.menu;

import main.manager.SoundManager;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class OptionsMenu extends JPanel implements MouseListener {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameState gameState;
    private BufferedImage optionsImage;
    private BufferedImage backgroundImage;
    private BufferedImage checkMarkImage;
    private final Polygon backButton;
    private final Rectangle musicCheckBox;
    private final Rectangle audioCheckBox;
    private boolean isMusicChecked;
    private boolean isAudioChecked;
    private final int screenWidth;
    private final int screenHeight;
    private static final int MENU_WIDTH = 900;
    private static final int MENU_HEIGHT = 700;
    private boolean debugMode = false;

    public OptionsMenu(CardLayout cardLayout, JPanel cardPanel, GameState gameState) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameState = gameState;

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight() - 30;

        // Calculate menu position
        int menuX = (screenWidth - MENU_WIDTH) / 2;
        int menuY = (screenHeight - MENU_HEIGHT) / 2;

        // Initialize back button polygon relative to menu position
        int[] backX = {70, 115, 115, 198, 198, 115, 115};
        int[] backY = {188, 150, 171, 171, 206, 205, 227};
        backButton = createMenuButtonPolygon(menuX, menuY, backX, backY);

        // Initialize checkboxes with provided coordinates
        musicCheckBox = new Rectangle(
                menuX + 645, menuY + 267,
                43, 43
        );

        audioCheckBox = new Rectangle(
                menuX + 645, menuY + 334,
                43, 43
        );

        loadSettings();
        loadImages();
        setupPanel();
    }

    private void setupPanel() {
        setLayout(null);
        addMouseListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
    }

    private Polygon createMenuButtonPolygon(int menuX, int menuY, int[] xPoints, int[] yPoints) {
        int[] adjustedX = new int[xPoints.length];
        int[] adjustedY = new int[yPoints.length];

        // Adjust coordinates relative to menu position
        for (int i = 0; i < xPoints.length; i++) {
            adjustedX[i] = menuX + xPoints[i];
            adjustedY[i] = menuY + yPoints[i];
        }

        return new Polygon(adjustedX, adjustedY, xPoints.length);
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/blured.JPG")));
            optionsImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/menu/options/options.png")));
            checkMarkImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/menu/options/V.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        Preferences prefs = Preferences.userNodeForPackage(OptionsMenu.class);
        SoundManager soundManager = gameState.getSoundManager();

        // Load preferences and sync with SoundManager's current state
        if (soundManager != null) {
            isMusicChecked = soundManager.isMusicEnabled();
            isAudioChecked = soundManager.isAudioEnabled();
        } else {
            // Fallback to preferences if SoundManager is not available
            isMusicChecked = prefs.getBoolean("musicEnabled", true);
            isAudioChecked = prefs.getBoolean("audioEnabled", true);
        }
    }


    private void saveSettings() {
        Preferences prefs = Preferences.userNodeForPackage(OptionsMenu.class);
        prefs.putBoolean("musicEnabled", isMusicChecked);
        prefs.putBoolean("audioEnabled", isAudioChecked);

        // Apply settings immediately
        applySettings();
    }

    private void applySettings() {
        SoundManager soundManager = gameState.getSoundManager();
        if (soundManager != null) {
            soundManager.setMusicEnabled(isMusicChecked);
            soundManager.setAudioEnabled(isAudioChecked);

            // Restart menu music if it should be playing
            if (isMusicChecked) {
                soundManager.startMenuMusic();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, null);
        }

        // Draw options menu image
        if (optionsImage != null) {
            int x = (screenWidth - MENU_WIDTH) / 2;
            int y = (screenHeight - MENU_HEIGHT) / 2;
            g2d.drawImage(optionsImage, x, y, MENU_WIDTH, MENU_HEIGHT, null);
        }

        // Draw checkmarks if enabled
        if (checkMarkImage != null) {
            if (isMusicChecked) {
                g2d.drawImage(checkMarkImage,
                        musicCheckBox.x, musicCheckBox.y,
                        musicCheckBox.width, musicCheckBox.height, null);
            }
            if (isAudioChecked) {
                g2d.drawImage(checkMarkImage,
                        audioCheckBox.x, audioCheckBox.y,
                        audioCheckBox.width, audioCheckBox.height, null);
            }
        }

        // Debug visualization
        if (debugMode) {
            g2d.setColor(new Color(255, 0, 0, 128));
            g2d.fillPolygon(backButton);
            g2d.setColor(Color.RED);
            g2d.drawPolygon(backButton);

            g2d.setColor(new Color(0, 255, 0, 128));
            g2d.drawRect(musicCheckBox.x, musicCheckBox.y,
                    musicCheckBox.width, musicCheckBox.height);

            g2d.setColor(new Color(0, 0, 255, 128));
            g2d.drawRect(audioCheckBox.x, audioCheckBox.y,
                    audioCheckBox.width, audioCheckBox.height);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (backButton.contains(p)) {
            cardLayout.show(cardPanel, "MainMenu");
        } else if (musicCheckBox.contains(p)) {
            isMusicChecked = !isMusicChecked;
            saveSettings();
            applySettings();
            repaint();
        } else if (audioCheckBox.contains(p)) {
            isAudioChecked = !isAudioChecked;
            saveSettings();
            applySettings();
            repaint();
        }
    }

    public static boolean isAudioEnabled() {
        Preferences prefs = Preferences.userNodeForPackage(OptionsMenu.class);
        return prefs.getBoolean("audioEnabled", true);
    }

    // Required interface methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}