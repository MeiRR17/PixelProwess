package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

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
    private static final int SPEED = 5;
    private final int playerWidth;
    private final int playerHeight;

    private static final int BOUND_WIDTH = 31;
    private static final int BOUND_HEIGHT = 30;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) throws IOException {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        this.screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
        this.screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

        playerWidth = (int) (gamePanel.tileSize * 1.5);
        playerHeight = (int) (gamePanel.tileSize * 1.5);

        this.bounds = new Rectangle(32, 51, BOUND_WIDTH, BOUND_HEIGHT);

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
        upStand = loadImage("/player/front-stand.png");
        upMove1 = loadImage("/player/front-walk.png");
        upMove2 = loadImage("/player/front-walk2.png");

        downStand = loadImage("/player/behind-stand.png");
        downMove1 = loadImage("/player/behind-walk.png");
        downMove2 = loadImage("/player/behind-walk2.png");

        rightStand = loadImage("/player/right-stand.png");
        rightMove1 = loadImage("/player/right-walk.png");
        rightMove2 = loadImage("/player/right-walk2.png");

        leftStand = loadImage("/player/left-stand.png");
        leftMove1 = loadImage("/player/left-walk.png");
        leftMove2 = loadImage("/player/left-walk2.png");
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

    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSpriteImage();


        g2.drawImage(image, screenX, screenY, playerWidth, playerHeight, null);
        drawBounds(g2);
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
    }
}
