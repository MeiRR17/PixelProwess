package object;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Bullet {
    public int x, y;
    public double angle; // Direction of the bullet
    public final int speed = 30;
    private BufferedImage bulletImage;
    public boolean bulletCollision = false;
    public Rectangle bounds;

    public Bullet(int x, int y, double angle) throws IOException {
        this.x = x;
        this.y = y;
        this.angle = angle;
        bounds = new Rectangle(x, y, 16, 16);
        bulletImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("object/weapon/bullet/rifle.png")));
    }

    public void update() {
        // Update the position of the bullet based on its angle and speed
        x += (int) (speed * Math.cos(angle));
        y += (int) (speed * Math.sin(angle));
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(bulletImage, x, y, 10, 10, null); // Adjust bullet size if necessary
        drawRedBox(g2);
    }
    public void drawRedBox(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawRect(x, y, bounds.width, bounds.height);
    }
}
