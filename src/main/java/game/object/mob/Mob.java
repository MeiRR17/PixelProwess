package game.object.mob;

import main.data.MobLevelGenerator;
import game.entity.Entity;
import game.entity.Player;
import main.menu.GamePanel;
import game.map.Map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.awt.geom.Ellipse2D;
import static game.object.weapon.Weapon.random;

public abstract class Mob extends Entity {
    protected GamePanel gamePanel;
    protected Player player;
    protected Map map;
    protected int health;
    protected double speedMultiplier;
    protected double damageMultiplier;
    protected int baseSpeed = 2;
    protected int baseDamage = 10;

    // Remove static size constants and make them instance variables
    public final int MOB_WIDTH;
    public final int MOB_HEIGHT;

    // Animation states
    public String currentState = "walk";
    protected String direction = "down";
    public int currentFrame = 0;
    protected int animationCounter = 0;
    protected int animationSpeed = 10;
    public int worldX;
    public int worldY;

    // Sprite storage
    protected BufferedImage[][][] sprites;
    protected final String[] states = {"walk", "attack", "hurt", "death"};
    protected final String[] directions = {"down", "left", "right", "up"};
    public boolean isDead = false;
    protected boolean isHurt = false;
    protected boolean isAttacking = false;
    protected long lastAttackTime = 0;
    protected long lastHurtTime = 0;
    protected int attackCooldown = 1000;
    protected int damage;

    protected Rectangle worldCollisionBox;
    protected boolean debug = false;
    protected int maxHealth;

    protected Polygon collisionTriangle;
    protected Point[] trianglePoints;
    protected Ellipse2D.Double collisionCircle;
    protected int collisionRadius; // Radius of the collision circle
    protected PathFinder pathFinder;
    protected List<Point> currentPath;
    protected int pathIndex;
    protected int pathUpdateCounter = 0;
    protected final int PATH_UPDATE_INTERVAL = 60; // Update path every 60 frames

    protected int level;
    protected double statMultiplier;
    protected Rectangle bounds;  // Collision bounds
    protected int scoreValue;


    public Mob(GamePanel gamePanel, Player player, Map map,
               double speedMult, double damageMult, int maxHealth, int mobSize, int playerLevel) {
        super(gamePanel);
        this.level = MobLevelGenerator.generateMobLevel(playerLevel);
        this.statMultiplier = MobLevelGenerator.getMobStatMultiplier(level);
        this.gamePanel = gamePanel;
        this.player = player;
        this.map = map;
        this.speedMultiplier = speedMult;
        this.damageMultiplier = damageMult;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = (int) (baseSpeed * speedMultiplier);
        initializeStats();
        this.scoreValue = calculateScoreValue();
        // Set mob-specific size
        this.MOB_WIDTH = mobSize;
        this.MOB_HEIGHT = mobSize;

        // Set the collision radius (50% of mob size for simplicity)
        this.collisionRadius = mobSize / 2;

        // Initialize the collision circle
        this.collisionCircle = new Ellipse2D.Double(worldX, worldY, collisionRadius * 2, collisionRadius * 2);

        // Initialize other properties
        spawnAtRandomLocation();
        loadSprites();
        this.pathFinder = new PathFinder(gamePanel, map);
        this.currentPath = new ArrayList<>();
        this.pathIndex = 0;
        this.bounds = new Rectangle(worldX, worldY, gamePanel.tileSize, gamePanel.tileSize);
    }
    @Override
    public void takeDamage(int damage) {
        if (!isDead) {
            health -= damage;

            // Always trigger hurt state, even if recently hurt
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
    public boolean isDeathAnimationComplete() {
        // Check if we're in death state and animation has completed
        if (isDead && currentState.equals("death")) {
            // Get the total number of frames for the death animation
            // Most death animations have 8 frames (0-7)
            int totalDeathFrames = 8;

            // Return true when we've reached or passed the last frame
            return currentFrame >= totalDeathFrames - 1;
        }
        return false;
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

        // Update collision circle position
        updateCollisionCircle();
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
    protected void initializeStats() {
        // Apply level-based scaling to base stats
        maxHealth *= statMultiplier;
        baseSpeed *= statMultiplier;
        baseDamage *= statMultiplier;

        // Set current stats to scaled base stats
        health = (int) maxHealth;
        speed = (int) baseSpeed;
        damage = (int) baseDamage;
    }

    public int getLevel() {
        return level;
    }

    protected void updateCollisionCircle() {
        collisionCircle.setFrame(
                worldX + MOB_WIDTH / 2 - collisionRadius, // Center the circle
                worldY + MOB_HEIGHT / 2 - collisionRadius,
                collisionRadius * 2,
                collisionRadius * 2
        );
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
    protected int calculateScoreValue() {
        return (int)(50 * level * statMultiplier); // Base score of 50, scaled by level and stats
    }

    public int getScoreValue() {
        return scoreValue;
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


    public int getDeathFrameCount() {
        // Return the number of frames in the death animation
        // This should match your sprite sheet structure
        return 8; // If your death animation has 8 frames
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

    public abstract String getMobType();

    public int getExperienceValue() {
        // Base XP value scaled by mob level and stat multiplier
        return (int)(25 * level * statMultiplier);
    }

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




    protected void moveTowardsPlayer() {
        if (isAttacking) return;

        // Update path periodically or when it's empty
        pathUpdateCounter++;
        int dynamicUpdateInterval = PATH_UPDATE_INTERVAL + (random != null ? random.nextInt(5) : 0);

        if (pathUpdateCounter >= dynamicUpdateInterval || currentPath.isEmpty()) {
            // Calculate distance to player
            double distanceToPlayer = Math.sqrt(
                    Math.pow(worldX - player.playerX, 2) +
                            Math.pow(worldY - player.playerY, 2)
            );

            // Only recalculate path if far enough or path is empty
            if (distanceToPlayer > gamePanel.tileSize * 3 || currentPath.isEmpty()) {
                // Store the current target point if we're mid-path
                Point currentTarget = (!currentPath.isEmpty() && pathIndex < currentPath.size())
                        ? currentPath.get(pathIndex)
                        : null;

                // Find new path
                currentPath = pathFinder.findPath(worldX, worldY, player.playerX, player.playerY);

                // If we were mid-path, try to maintain continuity
                if (currentTarget != null) {
                    // Find the closest point in the new path to our current target
                    int closestIndex = 0;
                    double minDistance = Double.MAX_VALUE;
                    for (int i = 0; i < currentPath.size(); i++) {
                        Point p = currentPath.get(i);
                        double distance = Math.sqrt(
                                Math.pow(p.x - currentTarget.x, 2) +
                                        Math.pow(p.y - currentTarget.y, 2)
                        );
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestIndex = i;
                        }
                    }
                    pathIndex = closestIndex;
                } else {
                    pathIndex = 0;
                }
            }

            pathUpdateCounter = 0;
        }

        // Follow the current path
        if (!currentPath.isEmpty() && pathIndex < currentPath.size()) {
            Point target = currentPath.get(pathIndex);

            double dx = target.x - worldX;
            double dy = target.y - worldY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Move to next path point
            if (distance > speed) {
                dx = (dx / distance) * speed;
                dy = (dy / distance) * speed;

                // Update direction based on movement
                if (Math.abs(dx) > Math.abs(dy)) {
                    direction = dx > 0 ? "right" : "left";
                } else {
                    direction = dy > 0 ? "down" : "up";
                }

                // Apply movement
                worldX += dx;
                worldY += dy;
            } else {
                // Close enough to current point, move to next one
                worldX = target.x;
                worldY = target.y;
                pathIndex++;
            }
        }
    }

    public Rectangle getWorldCollisionBox() {
        // Create a precise collision box
        int boxWidth = MOB_WIDTH / 2;
        int boxHeight = MOB_HEIGHT / 2;

        int boxX = worldX + (MOB_WIDTH - boxWidth) / 2;
        int boxY = worldY + (MOB_HEIGHT - boxHeight) / 2;

        return new Rectangle(boxX, boxY, boxWidth, boxHeight);
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

            // Draw collision rectangle for debugging
            if (gamePanel.debug) {
                g2.setColor(Color.GREEN); // Set color to green for collision rectangle
                g2.drawRect(
                        screenX,
                        screenY,
                        MOB_WIDTH,
                        MOB_HEIGHT
                );
            }
        }
        // Draw path for debugging
        if (gamePanel.debug && currentPath != null) {
            g2.setColor(Color.YELLOW);
            for (int i = pathIndex; i < currentPath.size() - 1; i++) {
                Point current = currentPath.get(i);
                Point next = currentPath.get(i + 1);

                int screenX1 = current.x - gamePanel.player.playerX + gamePanel.player.screenX;
                int screenY1 = current.y - gamePanel.player.playerY + gamePanel.player.screenY;
                int screenX2 = next.x - gamePanel.player.playerX + gamePanel.player.screenX;
                int screenY2 = next.y - gamePanel.player.playerY + gamePanel.player.screenY;

                g2.drawLine(screenX1, screenY1, screenX2, screenY2);
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

        // Draw level text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String levelText = "Lvl " + level;
        FontMetrics fm = g2.getFontMetrics();
        int levelWidth = fm.stringWidth(levelText);

        // Position level text to the left of the health bar
        int levelX = screenX - levelWidth - 5; // 5 pixels gap between text and health bar
        int levelY = screenY + yOffset + barHeight; // Align with health bar

        // Draw level text
        g2.drawString(levelText, levelX, levelY);

        // Health remaining (green)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int)((barWidth * health) / getMaxHealth());
        g2.fillRect(screenX, screenY + yOffset, currentHealthWidth, barHeight);
    }

    protected abstract int getMaxHealth();

    protected boolean checkCollision(int newX, int newY) {
        // Create a test circle at the new position
        Ellipse2D.Double testCircle = new Ellipse2D.Double(
                newX + MOB_WIDTH / 2 - collisionRadius,
                newY + MOB_HEIGHT / 2 - collisionRadius,
                collisionRadius * 2,
                collisionRadius * 2
        );

        // Check tile collisions
        int col = newX / gamePanel.tileSize;
        int row = newY / gamePanel.tileSize;

        for (int c = col - 1; c <= col + 1; c++) {
            for (int r = row - 1; r <= row + 1; r++) {
                if (c >= 0 && c < gamePanel.worldColumn && r >= 0 && r < gamePanel.worldRow) {
                    int tileNum = gamePanel.map.mapNumber[c][r];
                    if (gamePanel.map.tiles[tileNum].collision || gamePanel.map.tiles[tileNum].customCollision) {
                        Rectangle tileRect = new Rectangle(
                                c * gamePanel.tileSize,
                                r * gamePanel.tileSize,
                                gamePanel.tileSize,
                                gamePanel.tileSize
                        );
                        if (testCircle.intersects(tileRect)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int getHealth() {
        return health;
    }
}

// Specific mob implementations
