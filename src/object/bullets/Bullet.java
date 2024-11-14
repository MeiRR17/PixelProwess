package object.bullets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    public double angle; // Direction of the bullet
    public final int speed = 3;
    public BufferedImage image;
    public boolean bulletCollision = false;
    public int damage;

    public Bullet(int x, int y, double angle, BufferedImage image, int damage) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.image = image;
        this.damage = damage;
    }

    public void update() {
        x += (int) (speed * Math.cos(angle)); // Move in the x direction
        y += (int) (speed * Math.sin(angle)); // Move in the y direction
    }

    public void draw(Graphics2D g2) {
        AffineTransform originalTransform = g2.getTransform();

        //Rotate bullet image based on the shooting angle
        g2.translate(x, y);
        g2.rotate(angle);

        //Draw bullet image at current position
        g2.drawImage(image, image.getWidth(), image.getHeight(), 10, 5, null);
        g2.setTransform(originalTransform);
    }

    public Rectangle calculateRectangle(){
        return new Rectangle(x, y, 10, 5);
    }
}
