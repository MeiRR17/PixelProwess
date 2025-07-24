package game.entity;

import main.data.DatabasePlayerLoader;
import main.data.PlayerLevelData;
import game.map.Collision;
import game.object.weapon.*;
import main.menu.OptionsMenu;
import game.object.mob.Mob;
import main.menu.GamePanel;
import game.object.weapon.weapons.Pistol;
import game.object.weapon.weapons.Shotgun;
import game.utility.KeyHandler;
import game.utility.MouseHandler;
import game.utility.MouseInfoUtil;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class Player extends Entity {
    private final GamePanel gamePanel;
    private final KeyHandler keyHandler;
    private Clip damageSound;

    public int health;

    public final int screenX;
    public final int screenY;

    private double angle;

    // Player image buffers
    private BufferedImage upStand, upMove1, upMove2;
    private BufferedImage downStand, downMove1, downMove2;
    private BufferedImage rightStand, rightMove1, rightMove2;
    private BufferedImage leftStand, leftMove1, leftMove2;

    // Player properties
    public static final int SPEED = 10;
    public final int playerWidth;
    public final int playerHeight;

    public static List<Bullet> bullets;

    private MouseHandler mouseHandler;
    private long lastShotTime = 0;

    private long lastPickupTime = 0;
    private static final long PICKUP_COOLDOWN = 1000;

    public Weapon currentWeapon;
    public Weapon smallWeapon;
    public Weapon bigWeapon;

    public BufferedImage currentBullet;

    private Weapon nearbyWeapon;

    public CharacterStats characterStats;
    private String characterName;
    private double baseCriticalHitChance = 0.05;
    private double baseDefense = 1.0;
    private BufferedImage healthBarPanel;
    private BufferedImage healthBarImage;
    private BufferedImage shieldBarImage;
    public int maxHealth;
    private int maxShield = 100;
    private int currentShield = 0;
    public List<Potion> potions;
    public int currentPotionIndex;

    private boolean isUsingPotion = false;
    private int level;
    private DatabasePlayerLoader dbLoader;

    private int experience;
    private Map<String, Integer> mobKillCounts;
    private static final int PICKUP_RANGE = 64;
    private boolean isDying = false;
    private boolean isVisible = true;
    public GameOverHandler gameOverHandler;
    private long lastDamageTime;

    public Player(GamePanel gamePanel, KeyHandler keyHandler, MouseHandler mouseHandler, DatabasePlayerLoader dbLoader) throws IOException {
        super(gamePanel);
        this.gamePanel = gamePanel;
        this.dbLoader = dbLoader;
        this.characterName = gamePanel.gameState.getSelectedCharacter().toLowerCase();
        this.level = loadPlayerLevel();
        this.health = 100;
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        PlayerLevelData levelData = dbLoader.loadPlayerData(gamePanel.gameState.getPlayerName(), characterName);
        this.level = levelData.getLevel();
        this.characterStats = new CharacterStats(characterName, dbLoader);
        applyCharacterStats();


        this.screenX = gamePanel.screenWidth / 2 - gamePanel.tileSize / 2;
        this.screenY = gamePanel.screenHeight / 2 - gamePanel.tileSize / 2;

        playerWidth = gamePanel.tileSize / 2;
        playerHeight = (int) ((double) gamePanel.tileSize / 2 * 1.45);

        this.bounds = new Rectangle(3, 13, 26, 26);

        solidAreaDefaultX = bounds.x;
        solidAreaDefaultY = bounds.y;
        setDefaultValues();

        loadPlayerImages();
        this.lastDamageTime = System.currentTimeMillis();



        bullets = new ArrayList<>();
        this.potions = new ArrayList<>();
        this.currentPotionIndex = 0;

        this.mobKillCounts = new HashMap<>();
        try {
            AudioInputStream damageAudioStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/sound effect/get damage.wav"));
            damageSound = AudioSystem.getClip();
            damageSound.open(damageAudioStream);
        } catch (UnsupportedAudioFileException | LineUnavailableException | NullPointerException e) {
            System.out.println("Error loading damage sound: " + e.getMessage());
            e.printStackTrace();
        }
        this.gameOverHandler = new GameOverHandler(gamePanel, this);
    }
    public void draw(Graphics2D g2) {
        if (!isVisible) return;

        // Handle death animation
        if (isDying) {
            // Get the current sprite image
            BufferedImage image = getCurrentSpriteImage();

            // Calculate fade based on time since death
            long deathTime = System.currentTimeMillis() - lastDamageTime;
            float fadeProgress = Math.min(1.0f, deathTime / 3000f); // 3 seconds to completely fade

            // Calculate float offset
            int floatOffset = 0;
            if (gameOverHandler != null) {
                floatOffset = gameOverHandler.getFloatYOffset();
            } else {
                floatOffset = (int)(fadeProgress * 100); // Simple float calculation
            }

            // Draw the player with float offset and fading effect
            Composite originalComposite = g2.getComposite();
            float alpha = Math.max(0, 1.0f - fadeProgress);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            g2.drawImage(image, screenX, screenY - floatOffset, playerWidth, playerHeight, null);

            // Reset composite
            g2.setComposite(originalComposite);

            // Once fully faded, set player to invisible
            if (fadeProgress >= 0.99f) {
                isVisible = false;
            }
        } else {
            // Draw the player's current sprite
            BufferedImage image = getCurrentSpriteImage();
            g2.drawImage(image, screenX, screenY, playerWidth, playerHeight, null);

            // Draw the health bar
            drawHealthBar(g2);

            // Check if the current weapon is not null before drawing
            if (currentWeapon != null) {
                // Calculate the angle between the player and the mouse
                double angle = calculateAngleToMouse();
                double angleOffset = Math.toRadians(-45); // Offset to align the weapon correctly
                angle += angleOffset;

                int radius = 85;

                // Calculate the weapon's position using the adjusted angle and radius
                int weaponX = (int) (screenX + (double) playerWidth / 2 + radius * Math.cos(angle));
                int weaponY = (int) (screenY + (double) playerHeight / 2 + radius * Math.sin(angle));

                // Correct gun alignment by adjusting its rotation and image rendering
                AffineTransform originalTransform = g2.getTransform();

                // Translate to the weapon position
                g2.translate(weaponX, weaponY);

                // Rotate the gun based on the calculated angle
                g2.rotate(angle);

                // Draw the current weapon image if it exists
                g2.drawImage(currentWeapon.gunImage, -currentWeapon.gunImage.getWidth() / 2, -currentWeapon.gunImage.getHeight() / 2, null);

                // Restore the original transform
                g2.setTransform(originalTransform);
            }

            // Draw bullets
            for (Bullet bullet : bullets) {
                bullet.draw(g2, gamePanel); // Draw the bullet
            }

            // Draw bounds for debugging
            drawBounds(g2);

            // Draw current potion if any
            if (!potions.isEmpty()) {
                Potion currentPotion = potions.get(currentPotionIndex);
                // Draw potion icon at a fixed position on screen
                g2.drawImage(currentPotion.image, 50, gamePanel.screenHeight - 100, 32, 32, null);
                // Draw potion count
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(String.valueOf(potions.size()), 85, gamePanel.screenHeight - 70);
            }        }

        // Let game over handler draw additional effects
        gameOverHandler.draw(g2);
    }
    public int getLevel() {
        return level;
    }

    private void saveProgress() {
        dbLoader.savePlayerProgress("defaultPlayer", characterName, level); // Replace "defaultPlayer" with actual player name
    }

    private int loadPlayerLevel() {
        PlayerLevelData levelData = dbLoader.loadPlayerData("defaultPlayer", characterName);
        return levelData.getLevel();
    }
    private void setDefaultValues() {
        if (gamePanel.mapTransitionManager != null) {
            gamePanel.mapTransitionManager.setInitialPlayerPosition();
        } else {
            playerX = gamePanel.tileSize * 50;
            playerY = gamePanel.tileSize * 42;
        }
        speed = SPEED;
        direction = "down";
    }

    private boolean isMoving() {
        return keyHandler.pressUp || keyHandler.pressDown || keyHandler.pressLeft || keyHandler.pressRight;
    }

    private void handleMovement() {
        updateDirection();
        checkCollisions();

        if (!playerCollision) {
            movePlayer();
        }
    }

    private void updateDirection() {
        if (keyHandler.pressUp) direction = "up";
        if (keyHandler.pressDown) direction = "down";
        if (keyHandler.pressRight) direction = "right";
        if (keyHandler.pressLeft) direction = "left";

        handleDiagonalMovement();
    }

    private void handleDiagonalMovement() {
        if (keyHandler.pressUp && keyHandler.pressRight) {
            direction = "up&right";
        } else if (keyHandler.pressUp && keyHandler.pressLeft) {
            direction = "up&left";
        } else if (keyHandler.pressDown && keyHandler.pressRight) {
            direction = "down&right";
        } else if (keyHandler.pressDown && keyHandler.pressLeft) {
            direction = "down&left";
        }
    }

    private void checkCollisions() {
        playerCollision = false;
        gamePanel.collisionCheck.checkTile(this);
    }

    private void movePlayer() {
        switch (direction) {
            case "up" -> playerY -= speed;
            case "down" -> playerY += speed;
            case "right" -> playerX += speed;
            case "left" -> playerX -= speed;
            case "up&right" -> moveDiagonally(-1, 1);
            case "up&left" -> moveDiagonally(-1, -1);
            case "down&right" -> moveDiagonally(1, 1);
            case "down&left" -> moveDiagonally(1, -1);
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (this.health <= 0) return; // Prevent multiple death triggers

        this.health -= damage;
        this.lastDamageTime = System.currentTimeMillis(); // Add this line

        if (this.health <= 0) {
            this.health = 0;
            handlePlayerDeath();
        }
        if (OptionsMenu.isAudioEnabled() && damageSound != null && !damageSound.isRunning()) {
            damageSound.setFramePosition(0);
            damageSound.start();
        }

        if (currentShield > 0) {
            if (damage <= currentShield) {
                currentShield -= damage;
                return;
            } else {
                damage -= currentShield;
                currentShield = 0;
            }
        }

        health = Math.max(0, health - damage);
    }
    private void handlePlayerDeath() {
        isDying = true;
        isVisible = true;

        // Force a 5-second delay before transitioning to menu
        // This is a fallback in case the GameOverHandler doesn't work
        new Thread(() -> {
            try {
                System.out.println("PLAYER DEATH: Starting 5-second forced delay");
                Thread.sleep(5000); // Force a 5-second delay
                System.out.println("PLAYER DEATH: 5-second delay completed");

                // Only transition if we're still dying (not already transitioned)
                if (isDying) {
                    System.out.println("PLAYER DEATH: Forcing transition to menu");
                    SwingUtilities.invokeLater(() -> {
                        if (gamePanel.gameState != null && gamePanel.gameState.getPlayMenu() != null) {
                            System.out.println("PLAYER DEATH: Final transition executing");
                            CardLayout cardLayout = (CardLayout) gamePanel.gameState.getPlayMenu().getParent().getLayout();
                            cardLayout.show(gamePanel.gameState.getPlayMenu().getParent(), "PlayMenu");
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void moveDiagonally(int yMultiplier, int xMultiplier) {
        int diagonalSpeed = (int) (speed / Math.sqrt(2));
        playerY += yMultiplier * diagonalSpeed;
        playerX += xMultiplier * diagonalSpeed;
    }

    private void moveBullet(){
        double adjustedSpeed = speed / Math.sqrt(2);
        for (int i = bullets.size() - 1; i >= 0; i--){
            switch (direction) {
                case "up" -> {
                    bullets.get(i).y += speed;
                }
                case "down" -> {
                    bullets.get(i).y -= speed;
                }
                case "right" -> {
                    bullets.get(i).x -= speed;
                }
                case "left" -> {
                    bullets.get(i).x += speed;
                }
                case "up&right" -> {
                    bullets.get(i).y += adjustedSpeed;
                    bullets.get(i).x -= adjustedSpeed;
                }
                case "up&left" -> {
                    bullets.get(i).y += adjustedSpeed;
                    bullets.get(i).x += adjustedSpeed;
                }
                case "down&right" -> {
                    bullets.get(i).y -= adjustedSpeed;
                    bullets.get(i).x -= adjustedSpeed;
                }
                case "down&left" -> {
                    bullets.get(i).y -= adjustedSpeed;
                    bullets.get(i).x += adjustedSpeed;
                }
            }
        }
    }

    private void updateSpriteAnimation() {
        spriteCounter++;
        if (spriteCounter > 7) {
            spriteNumber = (spriteNumber % 4) + 1; // Loop through sprite numbers 1 to 4
            spriteCounter = 0;
        }
    }

    private void drawHealthBar(Graphics2D g2) {
        // Fixed panel position and dimensions for 1920x1080
        int panelX = 40;
        int panelY = 50;
        int panelWidth = 500;
        int panelHeight = 115;

        // Calculate scaling factors relative to original panel size (179x41)
        double scaleX = (double) panelWidth / 179;
        double scaleY = (double) panelHeight / 41;

        // Draw the panel background
        g2.drawImage(healthBarPanel, panelX, panelY, panelWidth, panelHeight, null);

        // Scale all internal elements based on original positions but with new scaling
        int healthBarX = panelX + (int)(41 * scaleX);
        int healthBarY = panelY + (int)(12 * scaleY);
        int healthBarWidth = (int)((138 - 41) * scaleX);
        int healthBarHeight = (int)((19 - 11) * scaleY);

        // Shield bar remains unchanged
        int shieldBarX = panelX + (int)(40 * scaleX);
        int shieldBarY = panelY + (int)(26 * scaleY);
        int shieldBarWidth = (int)((135 - 40) * scaleX);
        int shieldBarHeight = (int)((29 - 26) * scaleY);

        // Health number position
        int healthNumberX = panelX + (int)(147 * scaleX);
        int healthNumberY = panelY + (int)(19 * scaleY);
        int healthNumberWidth = (int)((162 - 147) * scaleX);
        int healthNumberHeight = (int)((19 - 12) * scaleY);

        // Calculate current health percentage
        double healthPercentage = (double) health / maxHealth;
        int currentHealthWidth = (int)(healthBarWidth * healthPercentage);

        // Draw health bar
        if (currentHealthWidth > 0) {
            g2.drawImage(
                    healthBarImage,
                    healthBarX,
                    healthBarY,
                    healthBarX + currentHealthWidth,
                    healthBarY + healthBarHeight,
                    0,
                    0,
                    (int)(currentHealthWidth / scaleX),
                    (int)(healthBarHeight / scaleY),
                    null
            );
        }

        // Shield bar drawing remains the same
        if (currentShield > 0) {
            double shieldPercentage = (double) currentShield / maxShield;
            int currentShieldWidth = (int)(shieldBarWidth * shieldPercentage);

            g2.drawImage(
                    shieldBarImage,
                    shieldBarX,
                    shieldBarY,
                    shieldBarX + currentShieldWidth,
                    shieldBarY + shieldBarHeight,
                    0,
                    0,
                    (int)(currentShieldWidth / scaleX),
                    (int)(shieldBarHeight / scaleY),
                    null
            );
        }

        // Draw health number
        Color healthNumberColor = new Color(0xAE, 0x5D, 0x40);
        g2.setColor(healthNumberColor);

        int fontSize = (int)(healthNumberHeight * 0.9);
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));

        // Display actual health value instead of percentage
        String healthText = String.format("%d", health);
        FontMetrics metrics = g2.getFontMetrics();

        int textX = healthNumberX + (healthNumberWidth - metrics.stringWidth(healthText)) / 2;
        int textY = healthNumberY - (healthNumberHeight - metrics.getHeight()) / 2;

        g2.drawString(healthText, textX, textY);
    }

    private void drawGameOverNotification(Graphics2D g2) {
        String message = "You are down!";
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics metrics = g2.getFontMetrics();
        int messageWidth = metrics.stringWidth(message);
        int messageX = (screenX + playerWidth / 2) - (messageWidth / 2);
        int messageY = screenY + playerHeight / 2;

        g2.drawString(message, messageX, messageY);
    }




    private BufferedImage getCurrentSpriteImage() {
        return switch (direction) {
            case "up" -> getUpImage();
            case "down" -> getDownImage();
            case "right", "up&right", "down&right" -> getRightImage();
            case "left", "up&left", "down&left" -> getLeftImage();
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    private BufferedImage getDownImage() {
        return switch (spriteNumber) {
            case 1, 3 -> downStand;
            case 2 -> downMove1;
            case 4 -> downMove2;
            default -> throw new IllegalStateException("Unexpected sprite number: " + spriteNumber);
        };
    }

    private BufferedImage getUpImage() {
        return switch (spriteNumber) {
            case 1, 3 -> upStand;
            case 2 -> upMove1;
            case 4 -> upMove2;
            default -> throw new IllegalStateException("Unexpected sprite number: " + spriteNumber);
        };
    }

    private BufferedImage getRightImage() {
        return switch (spriteNumber) {
            case 1, 3 -> rightStand;
            case 2 -> rightMove1;
            case 4 -> rightMove2;
            default -> throw new IllegalStateException("Unexpected sprite number: " + spriteNumber);
        };
    }

    private BufferedImage getLeftImage() {
        return switch (spriteNumber) {
            case 1, 3 -> leftStand;
            case 2 -> leftMove1;
            case 4 -> leftMove2;
            default -> throw new IllegalStateException("Unexpected sprite number: " + spriteNumber);
        };
    }

    private void drawBounds(Graphics2D g2) {
        if (gamePanel.debug) {
            g2.setColor(Color.RED);
            g2.drawRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
        }
    }

    //weapon section

    @Override
    public void update() {
        gameOverHandler.checkGameOver();
        gameOverHandler.checkGameOver();

        if (gameOverHandler.isGameOver()) {
            updateSpriteAnimation();
            return;
        }
        if (isMoving()) {
            handleMovement();
            updateSpriteAnimation();
            if (!bullets.isEmpty() && !playerCollision) {
                moveBullet();
            }
        } else {
            spriteNumber = 1; // Reset to standing sprite
        }

        // Check for nearby weapons
        checkNearbyWeapons();

        if (keyHandler.pressDropWeapon) {
            dropWeapon(currentWeapon);
        }

        // Check if the player is trying to pick up a weapon
        long currentTime = System.currentTimeMillis();
        if (keyHandler.isEPressed && nearbyWeapon != null && (currentTime - lastPickupTime) > PICKUP_COOLDOWN) {
            for (Chest chest : gamePanel.objectPlacer.chests) {
                if (chest.isWithinRange(playerX, playerY, gamePanel.tileSize * 2)) {
                    chest.open();
                    keyHandler.isEPressed = false;
                    return;
                }
            }
        }
        if (keyHandler.pressUsePotion) {
            useCurrentPotion();
            return; // Skip the rest of the update if using a potion
        }
        // Handle weapon switching
        if (keyHandler.pressSmallWeapon && smallWeapon != null) {
            currentWeapon = smallWeapon;
            currentBullet = smallWeapon.bulletImage;
            keyHandler.pressSmallWeapon = false;
        }

        if (keyHandler.pressBigWeapon && bigWeapon != null) {
            currentWeapon = bigWeapon;
            currentBullet = bigWeapon.bulletImage;
            keyHandler.pressBigWeapon = false;
        }
        // Check for weapon switching
        if (keyHandler.pressSmallWeapon) {
            switchToSmallWeapon();
        }
        if (keyHandler.pressBigWeapon) {
            switchToBigWeapon();
        }

        // Check if shooting
        if (currentBullet != null && mouseHandler.isShooting()) {
            shoot();
        }

        // Check if the player is trying to reload
        if (keyHandler.pressReload && currentWeapon != null) {
            currentWeapon.reload();
        }

        // Handle reloading state
        if (currentWeapon != null) {
            currentWeapon.updateReload();
        }

        // Update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            if (Collision.checkCollision(bullet.calculateRectangle()) ||
                    bullet.y > 5120 || bullet.x > 5120 || bullet.y < 0 || bullet.x < 0) {
                bullets.remove(i);
            } else {
                bullet.update();
            }
        }

        checkNearbyWeapons();

        if (keyHandler.isEPressed && (currentTime - lastPickupTime) > PICKUP_COOLDOWN) {
            handleInteractions();
        }

        if (keyHandler.pressUsePotion && mouseHandler.isShooting()) {
            useCurrentPotion();
        }


        if (keyHandler.pressDropWeapon) {
            if (currentWeapon != null && !currentWeapon.isReloading) {
                dropWeapon(currentWeapon);
            }
            // Reset the drop weapon key press regardless of whether drop was successful
            keyHandler.pressDropWeapon = false;
        }
        if (!mouseHandler.isShooting() && currentWeapon != null) {
            currentWeapon.stopShootSound();
            mouseHandler.wasShootingLastFrame = false;
        }
    }


    private void checkNearbyWeapons() {
        nearbyWeapon = null;
        double closestDistance = Double.MAX_VALUE;

        for (Weapon weapon : gamePanel.weapons) {
            if (weapon != null) {
                double distance = Math.sqrt(
                        Math.pow(playerX - weapon.worldX, 2) +
                                Math.pow(playerY - weapon.worldY, 2)
                );

                if (distance < gamePanel.tileSize && distance < closestDistance) {
                    nearbyWeapon = weapon;
                    closestDistance = distance;
                }
            }
        }
    }

    private void fireShotgun() {
        if (!currentWeapon.isReloading) {
            // Play shotgun sound (single shot)
            currentWeapon.playShootSound();

            int numBullets = 5;
            double spread = 5;
            int damagePerPellet = currentWeapon.DAMAGE;

            for (int i = 0; i < numBullets; i++) {
                double pelletAngle = angle + Math.toRadians((i - (numBullets / 2)) * spread);
                createBullet(pelletAngle, damagePerPellet);
            }

            currentWeapon.ammoLeft--;
        }
    }

    private void fireNormalWeapon() {
        if (!currentWeapon.isReloading) {
            // Handle weapon sound based on type
            if (currentWeapon instanceof Pistol || !currentWeapon.isAutomatic) {
                currentWeapon.playShootSound(); // Play single shot sound
            } else {
                currentWeapon.playShootSound(); // For automatic weapons, the sound loop is managed
            }

            createBullet(angle, currentWeapon.DAMAGE);
            currentWeapon.ammoLeft--;
        }
    }

    private void createBullet(double bulletAngle, int damage) {
        // Adjust spawn position calculation
        double spawnDistance = 85;
        int centerX = playerX + playerWidth / 2;
        int centerY = playerY + playerHeight / 2;

        int bulletX = (int) (centerX + spawnDistance * Math.cos(bulletAngle));
        int bulletY = (int) (centerY + spawnDistance * Math.sin(bulletAngle));

        // Add bullet velocity normalization
        double speed = 15.0; // Adjust as needed
        double velocityX = speed * Math.cos(bulletAngle);
        double velocityY = speed * Math.sin(bulletAngle);

        bullets.add(new Bullet(bulletX, bulletY, bulletAngle, currentBullet, damage, velocityX, velocityY));
    }

    public void pickUpObject(Weapon weapon) {
        if (weapon == null) return;

        try {
            // Stop sound of current weapon if it's playing
            if (currentWeapon != null) {
                currentWeapon.stopShootSound();
            }

            Weapon weaponClone = weapon.clone();
            weaponClone.ammoLeft = weaponClone.MAGAZINE_SIZE;
            weaponClone.isReloading = false;
            weaponClone.collision = true;

            if (weaponClone instanceof Big) {
                if (bigWeapon != null) {
                    bigWeapon.stopShootSound(); // Stop sound of dropped weapon
                    dropWeapon(bigWeapon);
                }
                bigWeapon = (Big) weaponClone;
                currentWeapon = bigWeapon;
            } else if (weaponClone instanceof Small) {
                if (smallWeapon != null) {
                    smallWeapon.stopShootSound(); // Stop sound of dropped weapon
                    dropWeapon(smallWeapon);
                }
                smallWeapon = (Small) weaponClone;
                currentWeapon = smallWeapon;
            }

            if (currentWeapon != null) {
                currentBullet = currentWeapon.bulletImage;
            }

            removeWeaponFromWorld(weapon);
            gamePanel.inventoryDisplay.updateDisplay();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private double calculateAngleToMouse() {
        // Get player screen position
        int playerCenterX = screenX + playerWidth / 2;
        int playerCenterY = screenY + playerHeight / 2;

        // Get mouse position relative to the game panel
        Point mousePosition = MouseInfoUtil.getMousePosition();
        int mouseX = mousePosition.x - gamePanel.getLocationOnScreen().x;
        int mouseY = mousePosition.y - gamePanel.getLocationOnScreen().y;

        // Calculate the angle in radians
        double angle = Math.atan2(mouseY - playerCenterY, mouseX - playerCenterX);

        // Add 40 degrees offset to correct rifle alignment (convert 45 degrees to radians)

        return angle + Math.toRadians(45);
    }

    public void updateAngle(int mouseX, int mouseY) {
        // Calculate the angle based on the player's center and the mouse position
        int playerCenterX = screenX + playerWidth / 2;
        int playerCenterY = screenY + playerHeight / 2;

        // Calculate the angle using Math.atan2, which gives the angle in radians
        angle = Math.atan2(mouseY - playerCenterY, mouseX - playerCenterX);
    }

    public void useCurrentPotion() {
        if (!potions.isEmpty() && !isUsingPotion) {
            Potion potion = potions.get(currentPotionIndex);
            potion.use(this);
            potions.remove(currentPotionIndex);

            if (!potions.isEmpty()) {
                currentPotionIndex = Math.min(currentPotionIndex, potions.size() - 1);
            } else {
                currentPotionIndex = 0;
            }

            gamePanel.inventoryDisplay.updateDisplay();
        }
    }

    private void removeWeaponFromWorld(Weapon weapon) {
        for (int i = 0; i < gamePanel.weapons.length; i++) {
            if (gamePanel.weapons[i] == weapon) {
                gamePanel.weapons[i] = null;
                break;
            }
        }
    }

    public void dropWeapon(Weapon weapon) {
        if (weapon == null) return;

        // Prevent dropping weapon while reloading
        if (weapon.isReloading) {
            return;
        }

        try {
            // Stop any playing sounds for the weapon being dropped
            weapon.stopShootSound();

            Weapon droppedWeapon = weapon.clone();
            droppedWeapon.worldX = playerX;
            droppedWeapon.worldY = playerY;
            droppedWeapon.collision = true;
            droppedWeapon.ammoLeft = weapon.ammoLeft;
            droppedWeapon.isReloading = false;

            int emptySlot = gamePanel.getNextAvailableWeaponIndex();
            if (emptySlot != -1) {
                gamePanel.weapons[emptySlot] = droppedWeapon;
            }

            if (weapon == bigWeapon) {
                bigWeapon = null;
                if (currentWeapon == weapon) {
                    currentWeapon = smallWeapon;
                    currentBullet = (smallWeapon != null) ? smallWeapon.bulletImage : null;
                }
            } else if (weapon == smallWeapon) {
                smallWeapon = null;
                if (currentWeapon == weapon) {
                    currentWeapon = bigWeapon;
                    currentBullet = (bigWeapon != null) ? bigWeapon.bulletImage : null;
                }
            }

            gamePanel.inventoryDisplay.updateDisplay();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void handleInteractions() {
        long currentTime = System.currentTimeMillis();

        if (!keyHandler.isEPressed || (currentTime - lastPickupTime) <= PICKUP_COOLDOWN) {
            return;
        }

        // Handle chest interactions first
        for (Chest chest : new ArrayList<>(gamePanel.objectPlacer.chests)) {
            if (!chest.isOpen && isInRange(chest.worldX, chest.worldY, PICKUP_RANGE)) {
                chest.open();
                lastPickupTime = currentTime;
                keyHandler.isEPressed = false;
                return;
            }
        }

        // Then handle weapon pickup
        Weapon nearestWeapon = null;
        double closestDistance = PICKUP_RANGE;

        for (Weapon weapon : gamePanel.weapons) {
            if (weapon != null && weapon.collision) {
                double distance = getDistance(weapon.worldX, weapon.worldY);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    nearestWeapon = weapon;
                }
            }
        }

        if (nearestWeapon != null) {
            pickUpObject(nearestWeapon);
            lastPickupTime = currentTime;
            keyHandler.isEPressed = false;
        }
    }

    private boolean isInRange(int targetX, int targetY, int range) {
        double distance = Math.sqrt(
                Math.pow(playerX - targetX, 2) +
                        Math.pow(playerY - targetY, 2)
        );
        return distance <= range;
    }

    private double getDistance(int targetX, int targetY) {
        return Math.sqrt(Math.pow(playerX - targetX, 2) + Math.pow(playerY - targetY, 2));
    }

    public void shoot() {
        if (currentPotionIndex >= 0 && keyHandler.pressUsePotion) {
            return;
        }

        if (currentWeapon == null) {
            return;
        }

        // Stop shooting sound if weapon is reloading or out of ammo
        if (currentWeapon.isReloading || currentWeapon.ammoLeft <= 0) {
            currentWeapon.stopShootSound();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long shotDelay = (long) (1000 / currentWeapon.FIRE_RATE);

        // Check if enough time has passed since last shot
        if (currentTime - lastShotTime <= shotDelay) {
            return;
        }

        // Handle different weapon types
        if (currentWeapon instanceof Pistol) {
            // Pistol only shoots on initial click
            if (mouseHandler.isShooting() && !mouseHandler.wasShootingLastFrame) {
                fireWeapon();
            }
        } else {
            // All other weapons can shoot continuously
            if (mouseHandler.isShooting()) {
                fireWeapon();
            }
        }

        lastShotTime = currentTime;
        mouseHandler.wasShootingLastFrame = mouseHandler.isShooting();

        // Stop sound if not shooting anymore
        if (!mouseHandler.isShooting()) {
            currentWeapon.stopShootSound();
        }
    }

    private void fireWeapon() {
        if (currentWeapon instanceof Shotgun) {
            fireShotgun();
        } else {
            fireNormalWeapon();
        }
    }

    // points system

    public void gainExperience(Mob mob) {
        // Track kill count
        String mobType = mob.getMobType();
        mobKillCounts.put(mobType, mobKillCounts.getOrDefault(mobType, 0) + 1);

        // Add experience
        int xpGained = mob.getExperienceValue();
        experience += xpGained;

        // Check for level up
        checkLevelUp();

        // Save to database
        saveProgress();
    }

    private void checkLevelUp() {
        int xpNeeded = calculateXPForNextLevel();
        while (experience >= xpNeeded) {
            level++;
            experience -= xpNeeded;
            xpNeeded = calculateXPForNextLevel();
        }
    }

    private int calculateXPForNextLevel() {
        return (int)(100 * Math.pow(1.5, level - 1));
    }

    private void applyLevelUpBonuses() {
        // Increase max health
        maxHealth += 10;
        health = maxHealth; // Heal to full on level up

        // Increase base stats
        baseDefense += 0.05; // 5% more defense per level
        baseCriticalHitChance += 0.01; // 1% more crit chance per level

        // Update character stats
        characterStats.updateLevel(level);

        // Optional: Show level up message or effects
        System.out.println("Level Up! Now level " + level);
    }


    //load images

    public void addPotion(Potion potion) {
        potions.add(potion);
    }

    private void applyCharacterStats() {
        // Calculate max health based on base health and multiplier
        this.maxHealth = (int)(100 * characterStats.getHealthMultiplier());
        this.health = this.maxHealth; // Start with full health

        // Apply speed modifier
        this.speed = (int)(SPEED * characterStats.getSpeedMultiplier());

        // Store modified defense value
        this.baseDefense *= characterStats.getDefenseMultiplier();
    }

    public boolean isAlive() {
        return health > 0;
    }
    private void loadPlayerImages() throws IOException {
        // load images based on characterName instead of random selection
        upStand = loadImage("/player/" + characterName + "/up/stand.png");
        upMove1 = loadImage("/player/" + characterName + "/up/walk1.png");
        upMove2 = loadImage("/player/" + characterName + "/up/walk2.png");

        downStand = loadImage("/player/" + characterName + "/down/stand.png");
        downMove1 = loadImage("/player/" + characterName + "/down/walk1.png");
        downMove2 = loadImage("/player/" + characterName + "/down/walk2.png");

        rightStand = loadImage("/player/" + characterName + "/right/stand.png");
        rightMove1 = loadImage("/player/" + characterName + "/right/walk1.png");
        rightMove2 = loadImage("/player/" + characterName + "/right/walk2.png");

        leftStand = loadImage("/player/" + characterName + "/left/stand.png");
        leftMove1 = loadImage("/player/" + characterName + "/left/walk1.png");
        leftMove2 = loadImage("/player/" + characterName + "/left/walk2.png");

        // Load health bar panel
        String healthBarPath = "/player/" + characterName + "/HealthBarPanel.png";
        healthBarPanel = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(healthBarPath)));

        // Load health and shield bar images
        healthBarImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/UI/healthBarPanel/health.png")));
        shieldBarImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/UI/healthBarPanel/shield.png")));

    }

    public void addShield(int amount) {
        currentShield = Math.min(currentShield + amount, maxShield);
    }

    private BufferedImage loadImage(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Cannot find resource: " + path);
        }
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new IOException("Error loading image: " + path, e);
        } finally {
            is.close();
        }
    }

    private void switchToSmallWeapon() {
        if (smallWeapon != null) {
            currentWeapon = smallWeapon;
            currentBullet = smallWeapon.bulletImage;
        }
    }

    private void switchToBigWeapon() {
        if (bigWeapon != null) {
            currentWeapon = bigWeapon;
            currentBullet = bigWeapon.bulletImage;
        }
    }
    public boolean isDying() {
        return isDying;
    }


    public void setDying(boolean dying) {
        this.isDying = dying;
        // Reset death animation
        if (dying) {
            isVisible = true;
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
