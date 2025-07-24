package main;

import main.menu.GamePanel;
import main.menu.GameState;
import main.menu.OpeningScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    // Debug mode configuration
    private static final boolean DEBUG_MODE = false; // set to true to enable debug mode
    private static final String DEFAULT_DEBUG_CHARACTER = "pip"; //Default character for debug mode
    private static final int DEBUG_LEVEL = 1; // Default level for debug mode

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int screenWidth = (int) screenSize.getWidth();
                int screenHeight = (int) screenSize.getHeight() - 30;

                JFrame window = new JFrame();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(false);
                window.setTitle("Pixel Prowess");
                window.setBackground(Color.BLACK);

                initializeGame(window, screenWidth, screenHeight);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error initializing game: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    private static void initializeGame(JFrame window, int screenWidth, int screenHeight) {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GameState.getInstance().getSoundManager().cleanup();
            }
        });

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setPreferredSize(new Dimension(screenWidth, screenHeight));

        GameState gameState = GameState.getInstance();

        if (DEBUG_MODE) {
            gameState.setSelectedCharacter(DEFAULT_DEBUG_CHARACTER);
            gameState.setPlayerLevel(DEBUG_LEVEL);

            try {
                gameState.initializeGamePanel();
                GamePanel gamePanel = gameState.getGamePanel();
                gamePanel.gameSet();

                cardPanel.add(gamePanel, "GamePanel");
                window.add(cardPanel);
                window.pack();

                gamePanel.startGameThread();
                gamePanel.requestFocus();
                gamePanel.debug = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            OpeningScreen openingScreen = new OpeningScreen(gameState, cardLayout, cardPanel);
            openingScreen.setPreferredSize(new Dimension(screenWidth, screenHeight));
            cardPanel.add(openingScreen, "OpeningScreen");
            window.add(cardPanel);
            window.pack();
            openingScreen.requestFocus();
        }

        window.setLocation(-8, 0);
        window.setVisible(true);
    }
}