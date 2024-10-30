package object.bullets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Bullet {
    public int x, y;
    public double angle; // Direction of the bullet
    public final int speed = 10;
    private BufferedImage image;
    public boolean bulletCollision = false;

    protected String bulletPath;
    public BufferedImage bulletImage;
    public Bullet(String bulletPath, int x, int y, double angle) throws IOException {
        this.bulletPath = bulletPath;
        this.bulletImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(this.bulletPath)));
        this.x = x;
        this.y = y;
        this.angle = angle;
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
