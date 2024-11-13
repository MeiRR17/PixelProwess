package entity;

import main.Collision;
import main.GamePanel;
import object.bullets.Bullet;
import object.weapons.Weapon;
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

    public Weapon[] guns = new Weapon[5];

    public static List<Bullet> bullets;

    private boolean mousePressed = false; // Track mouse button state
    private MouseHandler mouseHandler;
    private long lastShotTime = 0; // Tracks the last time a shot was fired
    private final long shootingDelay = 200; // Shooting delay in milliseconds

    public Weapon currentWeapon;
    public BufferedImage currentBullet;


    public Player(GamePanel gamePanel, KeyHandler keyHandler, MouseHandler mouseHandler) throws IOException {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler; // Initialize mouseHandler

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



    private BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public void update() throws IOException {
        if (isMoving()) {
            handleMovement();
            int objIndex = gamePanel.collisionCheck.checkObject(this, true);
            pickUpObject(objIndex);
            updateSpriteAnimation();
            if (!bullets.isEmpty() && !playerCollision){
                moveBullet();
            }
        } else {
            spriteNumber = 1; // Reset to standing sprite
        }

        //check if shooting
        if (currentBullet != null && mouseHandler.isShooting()) {
            shoot(); // Call shoot method
        }

        // Update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            // Log the bullet's current position before checking collisions
            System.out.printf("Bullet at position X: %d, Y: %d%n", bullet.x, bullet.y);

            // Check the collision with walls, trees, etc.
            if (Collision.checkCollision(bullet.calculateRectangle()) ||
                    bullet.y > 5120 || bullet.x > 5120 || bullet.y < 0 || bullet.x < 0) {
                bullets.remove(i); // Remove the bullet when it collides or goes out of bounds
            } else {
                bullet.update(); // Update bullet's position if no collision
            }
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

    public void draw(Graphics2D g2) throws IOException {
        // Draw the player's current sprite
        BufferedImage image = getCurrentSpriteImage();
        g2.drawImage(image, screenX, screenY, playerWidth, playerHeight, null);

        // Calculate the angle between the player and the mouse
        double angle = calculateAngleToMouse();
        double angleOffset = Math.toRadians(-45); // Offset to align the weapon correctly
        angle += angleOffset;

        int radius = 85;

        // Calculate the weapon's position using the adjusted angle and radius
        int weaponX = (int) (screenX + (double) playerWidth / 2 + radius * Math.cos(angle));
        int weaponY = (int) (screenY + (double) playerHeight / 2 + radius * Math.sin(angle));

        // Draw the weapon path around the player (for visualization)
        drawWeaponPath(g2, radius);

        // Draw the green line extending from the weapon
        drawAngleLine(g2, angle, radius, weaponX, weaponY);

        // Correct gun alignment by adjusting its rotation and image rendering
        AffineTransform originalTransform = g2.getTransform();

        // Translate to the weapon position
        g2.translate(weaponX, weaponY);

        // Rotate the gun based on the calculated angle
        g2.rotate(angle);

        // Draw the current weapon image if it exists
        if (currentWeapon != null) {
            g2.drawImage(currentWeapon.gunImage, -currentWeapon.gunImage.getWidth() / 2, -currentWeapon.gunImage.getHeight() / 2, null);
        }

        // Restore the original transform
        g2.setTransform(originalTransform);

        // Draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g2); // Draw the bullet
        }
        drawBounds(g2);
    }

    public void shoot() throws IOException {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > shootingDelay) { // shootingDelay in milliseconds
            // Calculate the starting position of the bullet
            int bulletX = (int) (screenX + (double) playerWidth / 2 + 85 * Math.cos(angle));
            int bulletY = (int) (screenY + (double) playerHeight / 2 + 85 * Math.sin(angle));
            BufferedImage bulletImage = currentBullet;

            // Create a bullet with the current angle
            bullets.add(new Bullet(bulletX, bulletY, angle, bulletImage)); // Create and add the bullet
            lastShotTime = currentTime; // Update last shot time
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
        double adjustedAngle = angle + Math.toRadians(45);

        return adjustedAngle;
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
        //g2.drawRoundRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height,10,50);
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed; // Set mouse pressed state
    }

    public void pickUpObject(int i) {
        if (i != 999) {
            Weapon pickedWeapon = gamePanel.weapons[i]; // Get the weapon object
            if (pickedWeapon != null) {
                String objectName = pickedWeapon.weaponName;
                switch (objectName) {
                    case "tacticalAssaultRifle", "automaticSniper", "ak", "p90", "pistol", "sniper", "scar", "shotgun":
                        currentWeapon = pickedWeapon;
                        currentBullet = gamePanel.weapons[i].bulletImage;
                        gamePanel.weapons[i] = null; // Remove the weapon from the game
                        break;
                    default:
                        currentWeapon = pickedWeapon; // Set current weapon to the picked weapon
                        currentBullet = gamePanel.weapons[i].bulletImage;
                        gamePanel.weapons[i] = null; // Remove the weapon from the game
                        break;
                }
            }
        }
    }
    public void setMouseHandler(MouseHandler mouseHandler) {
        this.mouseHandler = mouseHandler;
    }
}