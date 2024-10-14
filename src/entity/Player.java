package entity;

import main.GamePanel;
import main.KeyHandler;
import main.ObjectPlacer;
import object.Gun;
import object.Scar;
import utility.MouseInfoUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import object.Rifle;

public class Player extends Entity {
    private final GamePanel gamePanel;
    private final KeyHandler keyHandler;

    public final int screenX;
    public final int screenY;

    // Player image buffers
    private BufferedImage upStand, upMove1, upMove2;
    private BufferedImage downStand, downMove1, downMove2;
    private BufferedImage rightStand, rightMove1, rightMove2;
    private BufferedImage leftStand, leftMove1, leftMove2;

    // Player properties
    private static final int SPEED = 15;
    public final int playerWidth;
    public final int playerHeight;

    private static final int BOUND_WIDTH = 31;
    private static final int BOUND_HEIGHT = 30;

    public Gun[] guns = new Gun[5];

    public Player(GamePanel gamePanel, KeyHandler keyHandler) throws IOException {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        this.screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
        this.screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

        playerWidth = gamePanel.tileSize;
        playerHeight = (int) (gamePanel.tileSize * 1.45);

        this.bounds = new Rectangle(32, 51, BOUND_WIDTH, BOUND_HEIGHT);
        PickGun pickGun = new PickGun(this);
        pickGun.getGun();

        solidAreaDefaultX = bounds.x;
        solidAreaDefaultY = bounds.y;

        setDefaultValues();
        loadPlayerImages();
    }

    private void setDefaultValues() {
        playerX = gamePanel.tileSize * 23;
        playerY = gamePanel.tileSize * 23;
        speed = SPEED;
        direction = "down";
    }

    private void loadPlayerImages() throws IOException {
        upStand = loadImage("/player/2/front-stand.png");
        upMove1 = loadImage("/player/2/front-walk1.png");
        upMove2 = loadImage("/player/2/front-walk2.png");

        downStand = loadImage("/player/2/behind-stand.png");
        downMove1 = loadImage("/player/2/behind-walk1.png");
        downMove2 = loadImage("/player/2/behind-walk2.png");

        rightStand = loadImage("/player/2/right-stand.png");
        rightMove1 = loadImage("/player/2/right-walk1.png");
        rightMove2 = loadImage("/player/2/right-walk2.png");

        leftStand = loadImage("/player/2/left-stand.png");
        leftMove1 = loadImage("/player/2/left-walk1.png");
        leftMove2 = loadImage("/player/2/left-walk2.png");
    }

    private BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public void update() {
        if (isMoving()) {
            handleMovement();
            updateSpriteAnimation();
        } else {
            spriteNumber = 1; // Reset to standing sprite
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

        // Adjust the angle to make sure the gun is aligned correctly with the mouse
        double angleOffset = Math.toRadians(-45); // Offset to align the gun properly
        angle += angleOffset;

        // Define the radius at which the gun is positioned
        int radius = 85;

        // Calculate the player's center coordinates
        int playerCenterX = screenX + playerWidth / 2;
        int playerCenterY = screenY + playerHeight / 2;

        // Calculate the weapon's position based on the angle and the radius around the player
        int weaponX = (int) (playerCenterX + radius * Math.cos(angle));
        int weaponY = (int) (playerCenterY + radius * Math.sin(angle));

        // Draw the circular path around the player (for debugging)
        drawWeaponPath(g2, radius);

        // Draw a green line extending from the gun for debugging purposes
        drawAngleLine(g2, angle, radius, weaponX, weaponY);

        // Get the player's current gun
        Gun gun = guns[0]; // Ensure a gun is assigned to this slot

        if (gun != null) {
            // Set the gun's position based on the calculated position
            gun.worldX = weaponX;
            gun.worldY = weaponY;

            // Draw the gun at its calculated position and angle, passing the player's center for proper rotation
            gun.draw(g2, gamePanel, angle, playerCenterX, playerCenterY);
        } else {
            System.out.println("No gun assigned to player.");
        }

        // Draw the player's collision bounds
        drawBounds(g2);
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

    private BufferedImage getUpImage() {
        return switch (spriteNumber) {
            case 1, 3 -> downStand;
            case 2 -> downMove1;
            case 4 -> downMove2;
            default -> throw new IllegalStateException("Unexpected sprite number: " + spriteNumber);
        };
    }

    private BufferedImage getDownImage() {
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


}
