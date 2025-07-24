package main.menu;

import com.google.cloud.firestore.*;
import com.google.api.core.ApiFuture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

public class LeaderboardMenu extends JPanel implements MouseListener {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private BufferedImage leaderboardImage;
    private BufferedImage backgroundImage;
    private final int screenWidth, screenHeight;
    private final Polygon backButton;
    private final int MENU_WIDTH = 1200;
    private final int MENU_HEIGHT = 1000;
    private final GameState gameState;
    private ArrayList<LeaderboardEntry> topPlayers;
    private Font customFont;
    private int menuX, menuY;

    private static class LeaderboardEntry {
        String username;
        int score;

        LeaderboardEntry(String username, int score) {
            this.username = username;
            this.score = score;
        }
    }

    public LeaderboardMenu(CardLayout cardLayout, JPanel cardPanel, GameState gameState) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.gameState = gameState;
        this.topPlayers = new ArrayList<>();

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.width;
        this.screenHeight = screenSize.height - 30;

        // Calculate menu position (centered)
        this.menuX = (screenWidth - MENU_WIDTH) / 2;
        this.menuY = (screenHeight - MENU_HEIGHT) / 2;

        // Initialize back button polygon
        int[] backX = {100, 155, 155, 255, 255, 155, 155};
        int[] backY = {100, 52, 79, 80, 120, 120, 147};
        backButton = createMenuButtonPolygon(menuX, menuY, backX, backY);

        loadCustomFont();
        loadImages();
        setupPanel();
        fetchLeaderboardData();
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/LilitaOne-Regular.ttf");
            if (is != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(48f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
            } else {
                customFont = new Font("Arial", Font.BOLD, 48);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 48);
        }
    }

    private void fetchLeaderboardData() {
        Firestore db = gameState.getFirebaseManager().getDb();
        CollectionReference players = db.collection("players");

        // Query to get top 7 players ordered by highScore
        Query query = players.orderBy("highScore", Query.Direction.DESCENDING).limit(7);

        ApiFuture<QuerySnapshot> future = query.get();
        try {
            QuerySnapshot snapshot = future.get();
            topPlayers.clear();

            for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
                String username = document.getString("username");
                Long score = document.getLong("highScore");
                if (username != null && score != null) {
                    topPlayers.add(new LeaderboardEntry(username, score.intValue()));
                }
            }
            repaint();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        try {
            leaderboardImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/menu/leaderboard.png")));
            backgroundImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/blured.JPG")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Polygon createMenuButtonPolygon(int menuX, int menuY, int[] xPoints, int[] yPoints) {
        int[] adjustedX = new int[xPoints.length];
        int[] adjustedY = new int[yPoints.length];

        for (int i = 0; i < xPoints.length; i++) {
            adjustedX[i] = menuX + xPoints[i];
            adjustedY[i] = menuY + yPoints[i];
        }

        return new Polygon(adjustedX, adjustedY, xPoints.length);
    }

    private void setupPanel() {
        setLayout(null);
        addMouseListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, null);
        }

        // Draw leaderboard menu image
        if (leaderboardImage != null) {
            g2d.drawImage(leaderboardImage, menuX, menuY, MENU_WIDTH, MENU_HEIGHT, null);
        }

        // Set up font for drawing
        g2d.setFont(customFont);
        g2d.setColor(new Color(0, 74, 173)); // #004aad color

        // Draw leaderboard entries
        int[][] nameCoords = {
                {301, 305, 648, 382}, {301, 385, 648, 462}, {301, 465, 648, 542},
                {301, 545, 648, 622}, {301, 625, 648, 702}, {301, 705, 648, 782},
                {301, 785, 648, 862}
        };

        int[][] scoreCoords = {
                {652, 305, 1038, 382}, {652, 385, 1038, 462}, {652, 465, 1038, 542},
                {652, 545, 1038, 622}, {652, 625, 1038, 702}, {652, 705, 1038, 782},
                {652, 785, 1038, 862}
        };

        for (int i = 0; i < 7; i++) {
            if (i < topPlayers.size()) {
                LeaderboardEntry entry = topPlayers.get(i);

                // Adjust coordinates relative to menu position
                int[] adjustedNameCoords = {
                        menuX + nameCoords[i][0], menuY + nameCoords[i][1],
                        menuX + nameCoords[i][2], menuY + nameCoords[i][3]
                };

                int[] adjustedScoreCoords = {
                        menuX + scoreCoords[i][0], menuY + scoreCoords[i][1],
                        menuX + scoreCoords[i][2], menuY + scoreCoords[i][3]
                };

                // Draw username
                drawCenteredString(g2d, entry.username,
                        adjustedNameCoords[0], adjustedNameCoords[1],
                        adjustedNameCoords[2], adjustedNameCoords[3]);

                // Draw score
                drawCenteredString(g2d, String.valueOf(entry.score),
                        adjustedScoreCoords[0], adjustedScoreCoords[1],
                        adjustedScoreCoords[2], adjustedScoreCoords[3]);
            }
        }
    }

    private void drawCenteredString(Graphics2D g2d, String text, int x1, int y1, int x2, int y2) {
        FontMetrics metrics = g2d.getFontMetrics();
        int x = x1 + (x2 - x1 - metrics.stringWidth(text)) / 2;
        int y = y1 + ((y2 - y1 - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, x, y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (backButton.contains(p)) {
            cardLayout.show(cardPanel, "MainMenu");
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}