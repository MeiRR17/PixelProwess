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
    protected static final int MOB_WIDTH = 64;
    protected static final int MOB_HEIGHT = 64;

    // Animation states
    protected String currentState = "idle";
    protected String direction = "down";
    protected int currentFrame = 0;
    protected int animationCounter = 0;
    protected int animationSpeed = 10;
    protected int worldX, worldY, width, height;

    // Sprite storage
    protected BufferedImage[][][] sprites; // [state][direction][frame]
    protected final String[] states = {"idle", "walk", "attack", "hurt", "death"};
    protected final String[] directions = {"down", "left", "right", "up"};
    protected boolean isDead = false;
    protected boolean isHurt = false;
    protected boolean isAttacking = false;
    protected long lastAttackTime = 0;
    protected int attackCooldown = 1000;

    protected Rectangle worldCollisionBox;
    protected boolean debug = false;
    protected int maxHealth;

    public BaseMob(GamePanel gamePanel, Player player, TileManager tileManager,
                   double speedMult, double damageMult, int maxHealth) {
        this.gamePanel = gamePanel;
        this.player = player;
        this.tileManager = tileManager;
        this.speedMultiplier = speedMult;
        this.damageMultiplier = damageMult;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = (int)(baseSpeed * speedMultiplier);
        bounds = new Rectangle(12, 12, MOB_WIDTH - 24, MOB_HEIGHT - 24);
        worldCollisionBox = new Rectangle(0, 0, MOB_WIDTH, MOB_HEIGHT);

        // Initialize position randomly in the world
        spawnAtRandomLocation();
        loadSprites();
    }

    // Add random spawn method
    protected void spawnAtRandomLocation() {
        boolean validLocation = false;
        while (!validLocation) {
            worldX = random.nextInt(gamePanel.worldColumn) * gamePanel.tileSize;
            worldY = random.nextInt(gamePanel.worldRow) * gamePanel.tileSize;

            int col = worldX / gamePanel.tileSize;
            int row = worldY / gamePanel.tileSize;

            if (col < gamePanel.worldColumn && row < gamePanel.worldRow) {
                if (!tileManager.tiles[tileManager.mapNumber[col][row]].collision) {
                    validLocation = true;
                }
            }
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


    protected abstract String getMobType();

    protected void loadSprites() {
        sprites = new BufferedImage[states.length][4][8];
        try {
            // Load idle sprites (single frame per direction)
            for (int d = 0; d < directions.length; d++) {
                sprites[0][d][0] = loadSprite("idle/" + directions[d] + ".png");
            }

            // Load walk, attack, death animations (8 frames)
            for (int i = 1; i < 4; i++) {
                for (int d = 0; d < directions.length; d++) {
                    for (int f = 0; f < 8; f++) {
                        sprites[i][d][f] = loadSprite(states[i] + "/" + directions[d] + "/" + (f + 1) + ".png");
                    }
                }
            }

            // Load hurt animations (6 frames)
            for (int d = 0; d < directions.length; d++) {
                for (int f = 0; f < 6; f++) {
                    sprites[4][d][f] = loadSprite("hurt/" + directions[d] + "/" + (f + 1) + ".png");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/mobs/" + getMobType() + "/" + path)));
    }

    @Override
    public void takeDamage(int damage) {
        if (!isDead) {
            health -= damage;
            isHurt = true;
            currentFrame = 0;
            currentState = "hurt";

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
            int maxFrames = currentState.equals("hurt") ? 6 : 8;
            currentFrame = (currentFrame + 1) % maxFrames;
            animationCounter = 0;
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
            case "idle" -> 0;
            case "walk" -> 1;
            case "attack" -> 2;
            case "hurt" -> 3;
            case "death" -> 4;
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
        int col = newX / gamePanel.tileSize;
        int row = newY / gamePanel.tileSize;

        return col < 0 || col >= gamePanel.worldColumn ||
                row < 0 || row >= gamePanel.worldRow ||
                tileManager.tiles[tileManager.mapNumber[col][row]].collision;
    }

    public int getHealth() {
        return health;
    }
}

// Specific mob implementations





