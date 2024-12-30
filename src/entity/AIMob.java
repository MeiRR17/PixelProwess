package entity;

import main.GamePanel;
import tile.TileManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static object.weapons.Weapon.random;

public class AIMob extends Entity {
    private final GamePanel gamePanel;
    private final Player player;
    private final TileManager tileManager;
    private int health;
    private static final int MOB_WIDTH = 64;  // Width of the mob
    private static final int MOB_HEIGHT = 64; // Height of the mob
    protected int worldX, worldY, width, height;
    private final int damage = 10;
    private final int attackCooldown;
    private long lastAttackTime;
    private final Random random;
    private final BufferedImage[][] sprites; // 2D array for direction and frames
    private int currentFrame = 0;
    private int animationSpeed = 10; // Controls animation speed
    private int animationCounter = 0;
    private String direction = "down"; // Current direction


    public AIMob(GamePanel gamePanel, Player player, TileManager tileManager) {
        this.gamePanel = gamePanel;
        this.player = player;
        this.tileManager = tileManager;
        this.health = 100;
        this.speed = 2;
        this.width = 40;
        this.height = 40;
        this.attackCooldown = 1000;
        this.random = new Random();

        // Initialize sprites [4 directions][8 frames each]
        sprites = new BufferedImage[4][8];
        String[] directions = {"up", "down", "left", "right"};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) { // Fixed index range (0 to 7)
                sprites[i][j] = loadSprite(directions[i] + "/" + (j + 1) + ".png"); // Adjusted filename
            }
        }

        // Initialize position randomly in the world
        worldX = (int) (Math.random() * (gamePanel.worldColumn * gamePanel.tileSize));
        worldY = (int) (Math.random() * (gamePanel.worldRow * gamePanel.tileSize));

        bounds = new Rectangle(0, 0, MOB_WIDTH, MOB_HEIGHT); // Collision box
    }

    private BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/mob/" + path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public Rectangle getWorldCollisionBox() {
        return new Rectangle(
                worldX + bounds.x,  // Use worldX instead of screenX
                worldY + bounds.y,  // Use worldY instead of screenY
                bounds.width,
                bounds.height
        );
    }

    private void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        int healthBarWidth = MOB_WIDTH;
        int healthBarHeight = 5;
        int yOffset = -10;  // Draw health bar above mob

        // Background (red)
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY + yOffset, healthBarWidth, healthBarHeight);

        // Health remaining (green)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int)((healthBarWidth * health) / 100.0);
        g2.fillRect(screenX, screenY + yOffset, currentHealthWidth, healthBarHeight);
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Mob took " + damage + " damage! Health remaining: " + health);

        // Teleport to a random collision tile if health is above 0
        if (health > 0) {
            teleportToRandomCollisionTile();
        }
    }

    private void teleportToRandomCollisionTile() {
        List<Point> collisionTiles = findCollisionTiles();
        if (!collisionTiles.isEmpty()) {
            // Select a random collision tile
            Point randomTile = collisionTiles.get(random.nextInt(collisionTiles.size()));
            worldX = randomTile.x;
            worldY = randomTile.y;
        }
    }

    private List<Point> findCollisionTiles() {
        List<Point> collisionTiles = new ArrayList<>();

        for (int col = 0; col < gamePanel.worldColumn; col++) {
            for (int row = 0; row < gamePanel.worldRow; row++) {
                // Check if the tile has collision
                if (tileManager.tiles[tileManager.mapNumber[col][row]].collision) {
                    int worldX = col * gamePanel.tileSize;
                    int worldY = row * gamePanel.tileSize;
                    collisionTiles.add(new Point(worldX, worldY));
                }
            }
        }

        return collisionTiles;
    }

    private void spawnAtRandomLocation() {
        boolean validLocation = false;
        while (!validLocation) {
            // Generate random positions within world bounds
            worldX = random.nextInt(gamePanel.worldColumn) * gamePanel.tileSize;
            worldY = random.nextInt(gamePanel.worldRow) * gamePanel.tileSize;

            // Check if location is valid (not a collision tile)
            int col = worldX / gamePanel.tileSize;
            int row = worldY / gamePanel.tileSize;

            if (col < gamePanel.worldColumn && row < gamePanel.worldRow) {
                if (tileManager.tiles[tileManager.mapNumber[col][row]].collision == false) {
                    validLocation = true;
                }
            }
        }
    }

    public void update() {
        // Calculate direction to player
        int targetX = player.playerX;
        int targetY = player.playerY;

        double dx = targetX - worldX;
        double dy = targetY - worldY;

        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx = dx / length * speed;
            dy = dy / length * speed;

            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }

            // Move the mob
            int newWorldX = (int) (worldX + dx);
            if (!checkCollision(newWorldX, worldY)) {
                worldX = newWorldX;
            }

            int newWorldY = (int) (worldY + dy);
            if (!checkCollision(worldX, newWorldY)) {
                worldY = newWorldY;
            }
        }

        // Update animation frame
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            currentFrame = (currentFrame + 1) % 8; // Loop through 8 frames
            animationCounter = 0;
        }

        // Check for attack range
        double distance = Math.sqrt(Math.pow(worldX - targetX, 2) + Math.pow(worldY - targetY, 2));
        if (distance < gamePanel.tileSize) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime > attackCooldown) {
                player.takeDamage(damage);
                lastAttackTime = currentTime;
            }
        }
    }


    private boolean checkCollision(int newX, int newY) {
        // Convert world position to tile position
        int col = newX / gamePanel.tileSize;
        int row = newY / gamePanel.tileSize;

        // Check bounds
        if (col < 0 || col >= gamePanel.worldColumn || row < 0 || row >= gamePanel.worldRow) {
            return true;
        }

        // Check if tile has collision
        return tileManager.tiles[tileManager.mapNumber[col][row]].collision;
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;

        if (worldX + MOB_WIDTH > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - MOB_WIDTH < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + MOB_HEIGHT > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - MOB_HEIGHT < gamePanel.player.playerY + gamePanel.player.screenY) {

            // Get direction index
            int directionIndex = switch (direction) {
                case "up" -> 0;
                case "down" -> 1;
                case "left" -> 2;
                case "right" -> 3;
                default -> 1;
            };

            // Draw sprite
            g2.drawImage(sprites[directionIndex][currentFrame], screenX, screenY, MOB_WIDTH, MOB_HEIGHT, null);

            // Draw health bar
            drawHealthBar(g2, screenX, screenY);

            // Debug: draw collision box
            g2.setColor(Color.GREEN);
            g2.drawRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
        }
    }


    public int getHealth() {
        return health;
    }
}
