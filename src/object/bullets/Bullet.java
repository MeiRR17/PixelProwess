package object.bullets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    public double angle; // Direction of the bullet
    public final int speed = 70;
    public BufferedImage image;
    public boolean bulletCollision = false;
    public int damage;
    public int width;
    public int height;
    private int distanceTraveled; // Distance traveled by the bullet
    private final int maxDistance; // Maximum distance before damage is fully lost

    public Bullet(int x, int y, double angle, BufferedImage image, int damage) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.image = image;
        this.damage = damage;
        this.distanceTraveled = 0;
        this.maxDistance = 500; // Set maximum distance (adjust as needed)
    }

    public void update() {
        // Calculate the new position first
        int newX = x + (int) (speed * Math.cos(angle));
        int newY = y + (int) (speed * Math.sin(angle));

        // Update position
        x = newX;
        y = newY;

        // Update distance traveled
        distanceTraveled += speed;

        // Reduce damage based on distance traveled
        if (distanceTraveled > maxDistance) {
            damage = 0; // Bullet damage is zero after max distance
        } else {
            // Calculate damage reduction (linear reduction)
            damage = (int) (damage * (1 - (double) distanceTraveled / maxDistance));
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform originalTransform = g2.getTransform();

        // Rotate bullet image based on the shooting angle
        g2.translate(x, y);
        g2.rotate(angle);

        // Draw bullet image at current position
        g2.drawImage(image, image.getWidth(), image.getHeight(), 10, 5, null);
        g2.setTransform(originalTransform);
    }

    public Rectangle calculateRectangle() {
        return new Rectangle(x, y, 10, 5);
    }
}