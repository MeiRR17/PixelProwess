package entity;

import main.GamePanel;
import tile.TileManager;

import java.awt.*;
import java.util.Random;

public class AIMob extends Entity {
    private final GamePanel gp;
    private final Player player;
    private final TileManager tileManager;
    private int health;
    private final int maxHealth;
    private final int attackCooldown;
    private long lastAttackTime;
    protected int worldX, worldY, width, height;
    private final int damage = 10;
    private final Random random;

    public AIMob(GamePanel gp, Player player, TileManager tileManager) {
        this.gp = gp;
        this.player = player;
        this.tileManager = tileManager;
        this.health = 100;
        this.maxHealth = 100;
        this.speed = 2;
        this.width = 40;
        this.height = 40;
        this.attackCooldown = 1000;
        this.random = new Random();

        // Spawn at random valid location
        spawnAtRandomLocation();
    }

    private void spawnAtRandomLocation() {
        boolean validLocation = false;
        while (!validLocation) {
            // Generate random positions within world bounds
            worldX = random.nextInt(gp.worldColumn) * gp.tileSize;
            worldY = random.nextInt(gp.worldRow) * gp.tileSize;

            // Check if location is valid (not a collision tile)
            int col = worldX / gp.tileSize;
            int row = worldY / gp.tileSize;

            if (col < gp.worldColumn && row < gp.worldRow) {
                if (tileManager.tiles[tileManager.mapNumber[col][row]].collision == false) {
                    validLocation = true;
                }
            }
        }
    }

    public void update() {
        // Calculate direction to player in world coordinates
        int targetX = player.playerX;
        int targetY = player.playerY;

        // Calculate movement vector
        double dx = targetX - worldX;
        double dy = targetY - worldY;

        // Normalize the vector
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = dx / length * speed;
            dy = dy / length * speed;

            // Try to move in X direction
            int newWorldX = (int)(worldX + dx);
            if (!checkCollision(newWorldX, worldY)) {
                worldX = newWorldX;
            }

            // Try to move in Y direction
            int newWorldY = (int)(worldY + dy);
            if (!checkCollision(worldX, newWorldY)) {
                worldY = newWorldY;
            }
        }

        // Check for attack range and attack the player
        double distance = Math.sqrt(Math.pow(worldX - targetX, 2) + Math.pow(worldY - targetY, 2));
        if (distance < gp.tileSize) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime > attackCooldown) {
                player.takeDamage(damage);
                lastAttackTime = currentTime;
            }
        }
    }

    private boolean checkCollision(int newX, int newY) {
        // Convert world position to tile position
        int col = newX / gp.tileSize;
        int row = newY / gp.tileSize;

        // Check bounds
        if (col < 0 || col >= gp.worldColumn || row < 0 || row >= gp.worldRow) {
            return true;
        }

        // Check if tile has collision
        return tileManager.tiles[tileManager.mapNumber[col][row]].collision;
    }

    public void draw(Graphics2D g2) {
        // Calculate screen position based on world position and player's position
        int screenX = worldX - player.playerX + player.screenX;
        int screenY = worldY - player.playerY + player.screenY;

        // Only draw if on screen
        if (screenX + width > 0 &&
                screenX - width < gp.screenWidth &&
                screenY + height > 0 &&
                screenY - height < gp.screenHeight) {

            // Draw the mob
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, width, height);

            // Draw health bar
            int barWidth = width;
            int barHeight = 5;
            int healthBarX = screenX;
            int healthBarY = screenY - barHeight - 5;
            float healthPercentage = (float) health / maxHealth;

            g2.setColor(Color.BLACK);
            g2.fillRect(healthBarX, healthBarY, barWidth, barHeight);

            g2.setColor(Color.GREEN);
            g2.fillRect(healthBarX, healthBarY, (int)(barWidth * healthPercentage), barHeight);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    // Get mob's collision rectangle in world coordinates
    public Rectangle getWorldCollisionBox() {
        return new Rectangle(worldX, worldY, width, height);
    }
}
