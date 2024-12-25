package entity;

import main.Collision;
import main.GamePanel;
import object.bullets.Bullet;
import object.weapons.*;
import utility.KeyHandler;
import utility.MouseHandler;
import utility.MouseInfoUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Player extends Entity {
    private final GamePanel gamePanel;
    private final KeyHandler keyHandler;

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
    private static final int SPEED = 15;
    public final int playerWidth;
    public final int playerHeight;

    private static final int BOUND_WIDTH = 30;
    private static final int BOUND_HEIGHT = 30;

    public static List<Bullet> bullets;

    private MouseHandler mouseHandler;
    private long lastShotTime = 0; // Tracks the last time a shot was fired

    private long lastPickupTime = 0; // Tracks the last time a weapon was picked up
    private static final long PICKUP_COOLDOWN = 1000; // Cooldown duration in milliseconds (1 second)

    public Weapon currentWeapon;
    public Weapon smallGun;
    public Weapon bigGun;

    public BufferedImage currentBullet;

    private Weapon nearbyWeapon; // To store the nearby weapon

    public Player(GamePanel gamePanel, KeyHandler keyHandler, MouseHandler mouseHandler) throws IOException {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler; // Initialize mouseHandler

        this.health = 100;

        this.screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
        this.screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

        playerWidth = gamePanel.tileSize;
        playerHeight = (int) (gamePanel.tileSize * 1.45);

        this.bounds = new Rectangle(32, 51, BOUND_WIDTH, BOUND_HEIGHT);
        PickGun pickGun = new PickGun();
        pickGun.getGun();

        solidAreaDefaultX = bounds.x;
        solidAreaDefaultY = bounds.y;

        setDefaultValues();
        loadPlayerImages();
        bullets = new ArrayList<>();
    }

    private void setDefaultValues() {
        playerX = gamePanel.tileSize * 38;
        playerY = gamePanel.tileSize * 38;
        speed = SPEED;
        direction = "down";
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0; // Prevent health from going below zero
        }
        System.out.println("Player health after damage: " + health);
    }

    public boolean isAlive() {
        return health > 0;
    }


    private void loadPlayerImages() throws IOException {
        String[][] skins = {
                {"/player/skin1/up/stand.png", "/player/skin1/up/walk1.png", "/player/skin1/up/walk2.png"},
                {"/player/skin1/down/stand.png", "/player/skin1/down/walk1.png", "/player/skin1/down/walk2.png"},
                {"/player/skin1/right/stand.png", "/player/skin1/right/walk1.png", "/player/skin1/right/walk2.png"},
                {"/player/skin1/left/stand.png", "/player/skin1/left/walk1.png", "/player/skin1/left/walk2.png"},
                {"/player/skin2/up/stand.png", "/player/skin2/up/walk1.png", "/player/skin2/up/walk2.png"},
                {"/player/skin2/down/stand.png", "/player/skin2/down/walk1.png", "/player/skin2/down/walk2.png"},
                {"/player/skin2/right/stand.png", "/player/skin2/right/walk1.png", "/player/skin2/right/walk2.png"},
                {"/player/skin2/left/stand.png", "/player/skin2/left/walk1.png", "/player/skin2/left/walk2.png"},
                {"/player/skin3/up/stand.png", "/player/skin3/up/walk1.png", "/player/skin3/up/walk2.png"},
                {"/player/skin3/down/stand.png", "/player/skin3/down/walk1.png", "/player/skin3/down/walk2.png"},
                {"/player/skin3/right/stand.png", "/player/skin3/right/walk1.png", "/player/skin3/right/walk2.png"},
                {"/player/skin3/left/stand.png", "/player/skin3/left/walk1.png", "/player/skin3/left/walk2.png"},
                {"/player/skin4/up/stand.png", "/player/skin4/up/walk1.png", "/player/skin4/up/walk2.png"},
                {"/player/skin4/down/stand.png", "/player/skin4/down/walk1.png", "/player/skin4/down/walk2.png"},
                {"/player/skin4/right/stand.png", "/player/skin4/right/walk1.png", "/player/skin4/right/walk2.png"},
                {"/player/skin4/left/stand.png", "/player/skin4/left/walk1.png", "/player/skin4/left/walk2.png"},
        };

        Random rand = new Random();
        int skinIndex = rand.nextInt(4) * 4;

        BufferedImage[] standImages = new BufferedImage[4];
        BufferedImage[] move1Images = new BufferedImage[4];
        BufferedImage[] move2Images = new BufferedImage[4];

        for (int i = 0; i < 4; i++) {
            standImages[i] = loadImage(skins[skinIndex + i][0]);
            move1Images[i] = loadImage(skins[skinIndex + i][1]);
            move2Images[i] = loadImage(skins[skinIndex + i][2]);
        }

        // Assign loaded images to variables
        upStand = standImages[0]; upMove1 = move1Images[0]; upMove2 = move2Images[0];
        downStand = standImages[1]; downMove1 = move1Images[1]; downMove2 = move2Images[1];
        rightStand = standImages[2]; rightMove1 = move1Images[2]; rightMove2 = move2Images[2];
        leftStand = standImages[3]; leftMove1 = move1Images[3]; leftMove2 = move2Images[3];
    }


    private void checkNearbyWeapons() {
        nearbyWeapon = null; // Reset the nearby weapon
        for (int i = 0; i < gamePanel.weapons.length; i++) {
            Weapon weapon = gamePanel.weapons[i];
            if (weapon != null) {
                // Calculate the distance between the player and the weapon
                double distance = Math.sqrt(Math.pow(playerX - weapon.worldX, 2) + Math.pow(playerY - weapon.worldY, 2));
                if (distance < 100) { // Adjust the distance as needed
                    nearbyWeapon = weapon; // Store the nearby weapon
                    break; // Exit the loop if a nearby weapon is found
                }
            }
        }
    }

    public void pickUpObject(Weapon weapon) {
        if (weapon == null) {
            return; // Early exit if weapon is null
        }

        // Remove the weapon from the game before picking it up
        removeWeaponFromGame(weapon);

        // Check the type of weapon and manage current weapons accordingly
        if (isSmallGun(weapon)) {
            handleSmallGunPickup(weapon);
        } else if (isBigGun(weapon)) {
            handleBigGunPickup(weapon);
        } else {
            handleOtherWeaponPickup(weapon);
        }
    }

    private boolean isSmallGun(Weapon weapon) {
        return weapon instanceof Pistol;
    }

    private boolean isBigGun(Weapon weapon) {
        return weapon instanceof Shotgun ||
                weapon instanceof Scar ||
                weapon instanceof P90 ||
                weapon instanceof AK ||
                weapon instanceof TacticalAssaultRifle;
    }

    private void handleSmallGunPickup(Weapon weapon) {
        if (smallGun != null) {
            dropWeapon(smallGun); // Drop the current small gun
        }
        setSmallGun(weapon); // Set the small gun to the picked pistol
        currentWeapon = smallGun; // Update current weapon
    }

    private void handleBigGunPickup(Weapon weapon) {
        if (bigGun != null) {
            dropWeapon(bigGun); // Drop the current big gun
        }
        setBigGun(weapon); // Set the big gun to the picked weapon
        currentWeapon = bigGun; // Update current weapon
    }

    private void handleOtherWeaponPickup(Weapon weapon) {
        if (currentWeapon != null) {
            dropWeapon(currentWeapon); // Drop the current weapon
        }

        currentWeapon = weapon; // Set the current weapon to the picked weapon
        currentBullet = weapon.bulletImage; // Update the current bullet image
    }

    private void removeWeaponFromGame(Weapon weapon) {
        for (int i = 0; i < gamePanel.weapons.length; i++) {
            if (gamePanel.weapons[i] == weapon) {
                gamePanel.weapons[i] = null; // Remove the weapon from the game
                break;
            }
        }
    }

    private void dropWeapon(Weapon weapon) {
        if (weapon == null) {
            return; // Early exit if weapon is null
        }

        // Stop any ongoing reload process
        weapon.isReloading = false; // Stop reloading

        // Create a new instance to represent the dropped weapon in the world
        Weapon droppedWeapon = null; // Ensure you implement a clone method in the Weapon class
        try {
            droppedWeapon = weapon.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        droppedWeapon.worldX = playerX; // Set the dropped weapon's X position
        droppedWeapon.worldY = playerY; // Set the dropped weapon's Y position

        // Place the dropped weapon in the game world
        int index = gamePanel.getNextAvailableWeaponIndex();
        if (index != -1) {
            gamePanel.weapons[index] = droppedWeapon; // Assign the dropped weapon to the available index
        }

        // Reset the player's current weapon
        if (currentWeapon == weapon) {
            currentWeapon = null; // Remove the weapon from the player
            currentBullet = null; // Reset the bullet image
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    // Method to set the small gun
    public void setSmallGun(Weapon weapon) {
        this.smallGun = weapon;
    }

    // Method to set the big gun
    public void setBigGun(Weapon weapon) {
        this.bigGun = weapon;
    }



    public void update() {
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
        if (keyHandler.pressPickUpWeapon && nearbyWeapon != null && (currentTime - lastPickupTime) > PICKUP_COOLDOWN) {
            pickUpObject(nearbyWeapon); // Pass the nearby weapon to the pickup method
            lastPickupTime = currentTime; // Update the last pickup time
        }

        // Check for weapon switching
        if (keyHandler.pressSmallWeapon) {
            switchToSmallGun();
        }
        if (keyHandler.pressBigWeapon) {
            switchToBigGun();
        }

        // Check if shooting
        if (currentBullet != null && mouseHandler.isShooting()) {
            shoot(); // Call shoot method
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
                bullets.remove(i); // Remove the bullet when it collides or goes out of bounds
            } else {
                bullet.update(); // Update bullet's position if no collision
            }
        }
    }
    private void switchToSmallGun() {
        if (smallGun != null) {
            currentWeapon = smallGun; // Switch to the small gun (pistol)
            currentBullet = smallGun.bulletImage; // Set the current bullet image
        }
    }

    private void switchToBigGun() {
        if (bigGun != null) {
            currentWeapon = bigGun; // Switch to the big gun (shotgun)
            currentBullet = bigGun.bulletImage; // Set the current bullet image
        }
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
        int barWidth = 100; // Width of the health bar
        int barHeight = 10; // Height of the health bar
        int healthBarX = screenX + (playerWidth - barWidth) / 2; // Center the health bar
        int healthBarY = screenY - 20; // Position above the player

        // Draw the background of the health bar
        g2.setColor(Color.RED);
        g2.fillRect(healthBarX, healthBarY, barWidth, barHeight);

        // Draw the current health
        int currentHealthWidth = (int) ((barWidth * health) / 100); // Calculate width based on current health
        g2.setColor(Color.GREEN);
        g2.fillRect(healthBarX, healthBarY, currentHealthWidth, barHeight);
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

    public void draw(Graphics2D g2) {
        // Draw the player's current sprite
        BufferedImage image = getCurrentSpriteImage();
        g2.drawImage(image, screenX, screenY, playerWidth, playerHeight, null);

        // Draw the health bar
        drawHealthBar(g2);


        // Draw notification if the player is down
        if (!isAlive()) {
            drawGameOverNotification(g2);
        }

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
            bullet.draw(g2); // Draw the bullet
        }

        // Draw bounds for debugging
        drawBounds(g2);

        // Draw pickup message if a weapon is nearby
        if (nearbyWeapon != null) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("Press 'E' to pick up " + nearbyWeapon.weaponName, screenX, screenY - 30);
        }
    }
    public void shoot() {
        long currentTime = System.currentTimeMillis();

        // Calculate the delay between shots based on the fire rate
        if (currentWeapon != null) {
            long shotDelay = (long) (1000 / currentWeapon.FIRE_RATE); // Convert to milliseconds

            // Check if there is ammo left and the weapon is not reloading
            if (currentWeapon.ammoLeft > 0 && !currentWeapon.isReloading) {
                // If the current weapon is a pistol, allow only one shot per press
                if (currentWeapon instanceof Pistol || currentWeapon instanceof Shotgun) {
                    // Only fire if the left mouse button is pressed and enough time has passed
                    if (mouseHandler.isShooting() && (currentTime - lastShotTime > shotDelay)) {
                        fireBullet();
                        lastShotTime = currentTime; // Update last shot time
                        mouseHandler.shooting = false; // Reset shooting state to prevent continuous firing
                    }
                } else {
                    // For other weapons, allow continuous shooting if the mouse is pressed
                    if (mouseHandler.isShooting() && (currentTime - lastShotTime > shotDelay)) {
                        fireBullet();
                        lastShotTime = currentTime; // Update last shot time
                    }
                }
            }
        }
    }

    private void fireBullet() {
        // Calculate the starting position of the bullet
        int bulletX = (int) (screenX + (double) playerWidth / 2 + 85 * Math.cos(angle));
        int bulletY = (int) (screenY + (double) playerHeight / 2 + 85 * Math.sin(angle));
        BufferedImage bulletImage = currentBullet;

        // If the current weapon is a shotgun, shoot multiple bullets
        if (currentWeapon instanceof Shotgun) {
            int numBullets = 5; // Number of bullets to shoot
            double damagePerBullet = (double) currentWeapon.DAMAGE / numBullets; // Damage for each bullet

            // Create bullets in different directions
            for (int i = 0; i < numBullets; i++) {
                // Calculate the angle for each bullet
                double bulletAngle = angle + Math.toRadians((i - 2) * 10); // Spread bullets 10 degrees apart
                bullets.add(new Bullet(bulletX, bulletY, bulletAngle, bulletImage, (int) damagePerBullet));
            }
        } else {
            // Create a single bullet for other weapons
            bullets.add(new Bullet(bulletX, bulletY, angle, bulletImage, currentWeapon.DAMAGE));
        }

        currentWeapon.ammoLeft--; // Decrease the ammo count
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

    private void drawAngleLine(Graphics2D g2, double angle, int radius, int weaponX, int weaponY) {
        // Calculate the end point of the line
        int lineLength = 1000;

        int lineEndX = (int) (weaponX + Math.cos(angle) * lineLength);
        int lineEndY = (int) (weaponY + Math.sin(angle) * lineLength);

        g2.setColor(Color.GREEN);
        g2.drawLine(weaponX, weaponY, lineEndX, lineEndY); // Line from weapon to the end point
    }

    // Method to draw the circular path
    private void drawWeaponPath(Graphics2D g2, int radius) {
        // Calculate the center of the player in screen coordinates
        int playerCenterX = screenX + playerWidth / 2;
        int playerCenterY = screenY + playerHeight / 2;

        // Set the color and stroke for the circle
        g2.setColor(Color.BLUE); // Choose a color for the path
        g2.setStroke(new BasicStroke(2)); // Set the stroke width

        // Draw the circle around the player
        g2.drawOval(playerCenterX - radius, playerCenterY - radius, radius * 2, radius * 2);
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
        g2.setColor(Color.RED);
        g2.drawRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
    }


    public void setMouseHandler(MouseHandler mouseHandler) {
        this.mouseHandler = mouseHandler;
    }
}