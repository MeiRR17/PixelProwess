package game.object.weapon;

import main.menu.GamePanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    public double angle;
    public final int speed = 70;
    public BufferedImage image;
    public boolean bulletCollision = false;
    public int damage;
    private double distanceTraveled;
    private final double maxDistance = 2000; // Increased max distance
    private final double originalX;
    private final double originalY;
    public double velocityX;
    public double velocityY;

    public Bullet(int x, int y, double angle, BufferedImage image, int damage, double velocityX, double velocityY) {
        this.x = x;
        this.y = y;
        this.originalX = x;
        this.originalY = y;
        this.angle = angle;
        this.image = image;
        this.damage = damage;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.distanceTraveled = 0;
    }

    public void update() {
        // Update position using velocity
        x += velocityX;
        y += velocityY;

        // Calculate distance traveled
        distanceTraveled = Math.sqrt(
                Math.pow(x - originalX, 2) +
                        Math.pow(y - originalY, 2)
        );

        // Adjust damage based on distance if needed
        if (distanceTraveled > maxDistance) {
            bulletCollision = true;
        }
    }

    public void draw(Graphics2D g2, GamePanel gamePanel) {
        // Only draw if bullet is within screen bounds
        int screenX = x - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = y - gamePanel.player.playerY + gamePanel.player.screenY;

        if (isOnScreen(screenX, screenY, gamePanel)) {
            AffineTransform originalTransform = g2.getTransform();
            g2.translate(screenX, screenY);
            g2.rotate(angle);
            g2.drawImage(image, -5, -2, 10, 5, null);
            g2.setTransform(originalTransform);
        }
    }

    private boolean isOnScreen(int screenX, int screenY, GamePanel gamePanel) {
        return screenX >= -50 &&
                screenX <= gamePanel.screenWidth + 50 &&
                screenY >= -50 &&
                screenY <= gamePanel.screenHeight + 50;
    }

    public Rectangle calculateRectangle() {
        return new Rectangle(x - 5, y - 2, 10, 5);
    }
}