package main;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Sets the operation that will happen by default when the user initiates a "close" on this frame. You must specify one of the following choices:
        window.setResizable(false); // Sets whether this frame is resizable by the user.
        window.setTitle("Pixel Prowess");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null); //center of the screen
        window.setVisible(true); //shows or hide window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the app when the window is closed
        window.setResizable(false); // Make sure the window size stays fixed
        window.setTitle("Pixel Prowess");

        // Set a reasonable window size (e.g., 75% of your screen resolution)
        int windowWidth = 1620;  // 75% of your screen width (2160 * 0.75)
        int windowHeight = 1080; // 75% of your screen height (1440 * 0.75)
        window.setSize(windowWidth, windowHeight);

        // Create the GamePanel and add it to the window
        gamePanel = new GamePanel(window);
        window.add(gamePanel);

        // Center the window on your screen
        window.setLocationRelativeTo(null);

        // Make the window visible
        window.setVisible(true);

        // Initialize the game and start its thread
        gamePanel.gameSet();
        gamePanel.startGameThread();
    }
}