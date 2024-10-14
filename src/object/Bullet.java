package object;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Bullet {
    public int x, y;
    public double angle; // Direction of the bullet
    public final int speed = 40;
    private BufferedImage bulletImage;

    public Bullet(int x, int y, double angle) throws IOException {
        this.x = x;
        this.y = y;
        this.angle = angle;
        bulletImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/bullet/pistolBullet.png")));
    }

    public void update() {
        // Update the position of the bullet based on its angle and speed
        x += (int) (speed * Math.cos(angle));
        y += (int) (speed * Math.sin(angle));
        this.x += speed;
    }

    public boolean isOffScreen() {
        return x < 0 || x > 1920 || y < 0 || y > 1080;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(bulletImage, x, y, 10, 10, null); // Adjust bullet size if necessary
    }
}
