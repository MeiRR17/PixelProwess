package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Sets the operation that will happen by default when the user initiates a "close" on this frame. You must specify one of the following choices:
        window.setExtendedState(MAXIMIZED_BOTH);
//        window.setUndecorated(true);
        window.setResizable(false); // Sets whether this frame is resizable by the user.
        window.setTitle("Pixel Prowess");
        GraphicsEnvironment graphic=GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice= graphic.getDefaultScreenDevice();
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null); //center of the screen
        window.setVisible(true); //shows or hide window
        graphicsDevice.setFullScreenWindow(window);


        gamePanel.gameSet();
        gamePanel.startGameThread();
    }
}