package game.map;

import java.awt.*;
import java.awt.image.BufferedImage;
public class Tile {
    public BufferedImage image;
    public boolean collision = false;
    public boolean customCollision = false;
    public Rectangle[] collisionAreas = new Rectangle[4]; // Array to hold smaller collision areas
    public int[][] collisionShape;
}

