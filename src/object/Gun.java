package object;

import main.GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Gun {
    protected String name;
    public BufferedImage image;
    public int worldX;
    public int worldY;

    // Method to draw the gun
    // Method to draw the gun
    // Method to draw the gun
    public void draw(Graphics2D g2, GamePanel gamePanel, double angle, int playerCenterX, int playerCenterY) {
        if (image == null) {
            // Handle null image case
            System.out.println("Gun image not loaded properly for: " + name);
            return;
        }

        // Set the gun's dimensions
        int gunWidth = image.getWidth();
        int gunHeight = image.getHeight();

        // Save the original transformation
        AffineTransform originalTransform = g2.getTransform();

        // Translate to the weapon's current world coordinates (from Player's draw method)
        g2.translate(worldX, worldY);

        // Rotate the gun image around the player's center
        g2.rotate(angle, playerCenterX - worldX, playerCenterY - worldY);

        // Draw the gun image with its top-left corner at the translated origin
        g2.drawImage(image, -gunWidth / 2, -gunHeight / 2, gunWidth, gunHeight, null);

        // Restore the original transformation
        g2.setTransform(originalTransform);
    }


}
