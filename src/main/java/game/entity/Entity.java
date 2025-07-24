package game.entity;

import main.menu.GamePanel;

import java.awt.*;

public abstract class Entity {

    public int playerX, playerY;
    public int speed;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNumber = 1;
    public Rectangle bounds;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean playerCollision = false;

    public Entity(GamePanel gamePanel) {
    }

    protected Entity() {
    }

    public abstract void takeDamage(int damage);

    public abstract void update();
    public abstract void draw(Graphics2D g2);
}
