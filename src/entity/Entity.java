package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public int playerX, playerY;
    public int speed;
    public BufferedImage
            up_stand, up_move1, up_move2,
            down_stand, down_move1, down_move2,
            right_stand, right_move1, right_move2,
            left_stand, left_move1, left_move2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNumber = 1;
    public Rectangle bounds;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean playerCollision = false;//default
    public int worldX, worldY; // Position in the game world
}