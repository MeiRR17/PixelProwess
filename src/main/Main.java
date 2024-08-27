package main;

import javax.swing.*;
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
        gamePanel.startGameThread();
    }
}