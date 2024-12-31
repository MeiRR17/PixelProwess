package entity.ui;

import entity.Entity;
import entity.Player;
import main.GamePanel;
import tile.TileManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static object.weapons.Weapon.random;

public abstract class BaseMob extends Entity {
    protected GamePanel gamePanel;
    protected Player player;
    protected TileManager tileManager;
    protected int health;
    protected double speedMultiplier;
    protected double damageMultiplier;
    protected int baseSpeed = 2;
    protected int baseDamage = 10;

    // Remove static size constants and make them instance variables
    protected final int MOB_WIDTH;
    protected final int MOB_HEIGHT;

    // Animation states
    protected String currentState = "walk";
    protected String direction = "down";
    protected int currentFrame = 0;
    protected int animationCounter = 0;
    protected int animationSpeed = 10;
    protected int worldX, worldY;

    // Sprite storage
    protected BufferedImage[][][] sprites;
    protected final String[] states = {"walk", "attack", "hurt", "death"};
    protected final String[] directions = {"down", "left", "right", "up"};
    protected boolean isDead = false;
    protected boolean isHurt = false;
    protected boolean isAttacking = false;
    protected long lastAttackTime = 0;
    protected long lastHurtTime = 0;
    protected int attackCooldown = 1000;
    protected int hurtCooldown = 3000;

    protected Rectangle worldCollisionBox;
    protected boolean debug = false;
    protected int maxHealth;

    public BaseMob(GamePanel gamePanel, Player player, TileManager tileManager,
                   double speedMult, double damageMult, int maxHealth, int mobSize) {
        this.gamePanel = gamePanel;
        this.player = player;
        this.tileManager = tileManager;
        this.speedMultiplier = speedMult;
        this.damageMultiplier = damageMult;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = (int)(baseSpeed * speedMultiplier);

        // Set mob-specific size
        this.MOB_WIDTH = mobSize;
        this.MOB_HEIGHT = mobSize;

        // Adjust collision bounds based on mob size
        // Make the collision box 75% of the mob size and centered
        int boundSize = (int)(mobSize * 0.75);
        int offset = (mobSize - boundSize) / 2;
        bounds = new Rectangle(offset, offset, boundSize, boundSize);
        worldCollisionBox = new Rectangle(0, 0, boundSize, boundSize);

        spawnAtRandomLocation();
        loadSprites();
    }

    protected void spawnAtRandomLocation() {
        boolean validLocation = false;
        while (!validLocation) {
            worldX = random.nextInt(gamePanel.worldColumn) * gamePanel.tileSize;
            worldY = random.nextInt(gamePanel.worldRow) * gamePanel.tileSize;

            // Check multiple points for collision to ensure the mob fits
            validLocation = !checkCollision(worldX, worldY) &&
                    !checkCollision(worldX + MOB_WIDTH, worldY) &&
                    !checkCollision(worldX, worldY + MOB_HEIGHT) &&
                    !checkCollision(worldX + MOB_WIDTH, worldY + MOB_HEIGHT);
        }
    }

    // Add death animation update
    protected void updateDeathAnimation() {
        if (currentState.equals("death")) {
            updateAnimation();
            if (currentFrame == 7) { // Last frame of death animation
                isDead = true;
            }
        }
    }

    // Add hurt animation update
    protected void updateHurtAnimation() {
        if (currentState.equals("hurt")) {
            updateAnimation();
            if (currentFrame == 5) { // Last frame of hurt animation
                isHurt = false;
                currentState = "walk";
                currentFrame = 0;
            }
        }
    }

    protected BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image == null) return null;

        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Draw the original image to the resized image
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

        return resizedImage;
    }

    protected abstract String getMobType();

    private BufferedImage loadSprite(String state, String direction, int frame) {
        try {
            // Construct the path based on your directory structure
            String fullPath = "/mob/" + getMobType() + "/" + state + "/" + direction + "/" + frame + ".png";
            BufferedImage originalImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(fullPath)));

            // Resize the image to the mob's specific dimensions
            return resizeImage(originalImage, MOB_WIDTH, MOB_HEIGHT);
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading sprite: " +"/mob/" + getMobType() + "/" + state + "/" + direction + "/" + frame + ".png");
            return null;
        }
    }

    protected void loadSprites() {
        sprites = new BufferedImage[states.length][directions.length][8];

        // Load and resize sprites for each state, direction, and frame
        for (int s = 0; s < states.length; s++) {
            for (int d = 0; d < directions.length; d++) {
                for (int f = 0; f < 8; f++) {
                    sprites[s][d][f] = loadSprite(states[s], directions[d], f + 1);
                }
            }
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (!isDead && !isHurt && System.currentTimeMillis() - lastHurtTime > hurtCooldown) {
            health -= damage;
            isHurt = true;
            currentFrame = 0;
            currentState = "hurt";
            lastHurtTime = System.currentTimeMillis();

            if (health <= 0) {
                health = 0;
                isDead = true;
                currentFrame = 0;
                currentState = "death";
            }
        }
    }

    @Override
    public void update() {
        if (isDead) {
            updateDeathAnimation();
            return;
        }

        if (isHurt) {
            updateHurtAnimation();
            return;
        }

        // Update animation and movement
        updateAnimation();
        moveTowardsPlayer();
        checkAttack();
    }

    protected void updateAnimation() {
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            currentFrame = (currentFrame + 1) % 8;
            animationCounter = 0;

            // Handle end of animations
            if (currentState.equals("hurt") && currentFrame == 0) {
                isHurt = false;
                currentState = "walk";
            } else if (currentState.equals("attack") && currentFrame == 0) {
                isAttacking = false;
                currentState = "walk";
            } else if (currentState.equals("death") && currentFrame == 7) {
                isDead = true;
            }
        }
    }

    protected void moveTowardsPlayer() {
        if (!isAttacking) {
            double dx = player.playerX - worldX;
            double dy = player.playerY - worldY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                dx = (dx / distance) * speed;
                dy = (dy / distance) * speed;

                // Update direction based on movement
                if (Math.abs(dx) > Math.abs(dy)) {
                    direction = dx > 0 ? "right" : "left";
                } else {
                    direction = dy > 0 ? "down" : "up";
                }

                // Move if no collision
                if (!checkCollision(worldX + (int)dx, worldY)) {
                    worldX += dx;
                }
                if (!checkCollision(worldX, worldY + (int)dy)) {
                    worldY += dy;
                }
            }
        }
    }

    public Rectangle getWorldCollisionBox() {
        return new Rectangle(
                worldX + bounds.x,
                worldY + bounds.y,
                bounds.width,
                bounds.height
        );
    }

    protected void checkAttack() {
        double distance = Math.sqrt(Math.pow(worldX - player.playerX, 2) +
                Math.pow(worldY - player.playerY, 2));

        if (distance < MOB_WIDTH && !isAttacking &&
                System.currentTimeMillis() - lastAttackTime > attackCooldown) {
            isAttacking = true;
            currentFrame = 0;
            currentState = "attack";
            player.takeDamage((int)(baseDamage * damageMultiplier));
            lastAttackTime = System.currentTimeMillis();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;

        if (isOnScreen(screenX, screenY)) {
            // Draw sprite
            BufferedImage currentSprite = getCurrentSprite();
            if (currentSprite != null) {
                g2.drawImage(currentSprite, screenX, screenY, MOB_WIDTH, MOB_HEIGHT, null);
            }

            // Draw health bar
            drawHealthBar(g2, screenX, screenY);

            // Debug: draw collision box
            if (gamePanel.debug) {
                g2.setColor(Color.RED);
                g2.drawRect(screenX + bounds.x, screenY + bounds.y,
                        bounds.width, bounds.height);
            }
        }
    }

    protected BufferedImage getCurrentSprite() {
        int stateIndex = switch (currentState) {
            case "walk" -> 0;
            case "attack" -> 1;
            case "hurt" -> 2;
            case "death" -> 3;
            default -> 0;
        };

        int directionIndex = switch (direction) {
            case "down" -> 0;
            case "left" -> 1;
            case "right" -> 2;
            case "up" -> 3;
            default -> 0;
        };

        return sprites[stateIndex][directionIndex][currentFrame];
    }

    protected boolean isOnScreen(int screenX, int screenY) {
        return screenX + MOB_WIDTH > 0 &&
                screenX - MOB_WIDTH < gamePanel.screenWidth &&
                screenY + MOB_HEIGHT > 0 &&
                screenY - MOB_HEIGHT < gamePanel.screenHeight;
    }

    protected void drawHealthBar(Graphics2D g2, int screenX, int screenY) {
        int barWidth = MOB_WIDTH;
        int barHeight = 5;
        int yOffset = -10;

        // Background (red)
        g2.setColor(Color.RED);
        g2.fillRect(screenX, screenY + yOffset, barWidth, barHeight);

        // Health remaining (green)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int)((barWidth * health) / getMaxHealth());
        g2.fillRect(screenX, screenY + yOffset, currentHealthWidth, barHeight);
    }

    protected abstract int getMaxHealth();

    protected boolean checkCollision(int newX, int newY) {
        // Check corners of the collision box
        int[][] points = {
                {newX + bounds.x, newY + bounds.y},                    // Top-left
                {newX + bounds.x + bounds.width, newY + bounds.y},     // Top-right
                {newX + bounds.x, newY + bounds.y + bounds.height},    // Bottom-left
                {newX + bounds.x + bounds.width, newY + bounds.y + bounds.height} // Bottom-right
        };

        for (int[] point : points) {
            int col = point[0] / gamePanel.tileSize;
            int row = point[1] / gamePanel.tileSize;

            if (col < 0 || col >= gamePanel.worldColumn ||
                    row < 0 || row >= gamePanel.worldRow ||
                    tileManager.tiles[tileManager.mapNumber[col][row]].collision) {
                return true;
            }
        }
        return false;
    }
    public int getHealth() {
        return health;
    }
}

// Specific mob implementations
