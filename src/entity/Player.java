package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity{
    GamePanel gamePanel;
    KeyHandler keyHandler;

    public final int screenX;
    public final int screenY;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) throws IOException {

        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        screenX = gamePanel.screenWidth/2 - (gamePanel.tileSize/2);
        screenY = gamePanel.screenHeight/2 - (gamePanel.tileSize/2);
        bounds = new Rectangle(26, 38, 26, 34);
        setDefaultValue();
        getPlayerImage();
    }
    public void setDefaultValue() {
        playerX = gamePanel.tileSize * 23;
        playerY = gamePanel.tileSize * 23;
        speed = 8;
        direction = "down";
    }

    public void getPlayerImage() throws IOException {
        up_stand = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/front-stand.png")));
        up_move1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/front-walk.png")));
        up_move2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/front-walk2.png")));

        down_stand = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/behind-stand.png")));
        down_move1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/behind-walk.png")));
        down_move2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/behind-walk2.png")));

        right_stand = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/right-stand.png")));
        right_move1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/right-walk.png")));
        right_move2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/right-walk2.png")));

        left_stand = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/left-stand.png")));
        left_move1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/left-walk.png")));
        left_move2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/left-walk2.png")));
    }
    public void update() {
        if (keyHandler.pressUp || keyHandler.pressDown || keyHandler.pressLeft || keyHandler.pressRight) {

            if (keyHandler.pressUp) {
                direction = "up";
            }
            if (keyHandler.pressDown) {
                direction = "down";
            }
            if (keyHandler.pressRight) {
                direction = "right";
            }
            if (keyHandler.pressLeft) {
                direction = "left";
            }


            //check collision
            playerCollision = false;
            gamePanel.collisionCheck.checkTile(this);
            //while collision false player able to move
            if(!playerCollision){
                switch (direction) {
                    case "up":
                        playerY -= speed;
                        break;
                    case "down":
                        playerY += speed;
                        break;
                    case "right":
                        playerX += speed;
                        break;
                    case "left":
                        playerX -= speed;
                        break;
                }
            }
            //sprite is an image inside an animation
            spriteCounter++;
            if (spriteCounter > 5) {
                if (spriteNumber == 1) {
                    spriteNumber = 2;
                } else if (spriteNumber == 2) {
                    spriteNumber = 1;
                }
                spriteCounter = 0;
            }
        }
    }
    public void draw(Graphics2D g2){
        BufferedImage image = null;

        switch (direction){
            case "up":
                if(spriteNumber == 1){
                    image = down_move1;
                }
                if(spriteNumber == 2){
                    image = down_move2;
                }
                break;
            case "down":
                if(spriteNumber == 1) {
                    image = up_move1;
                }
                if(spriteNumber == 2) {
                    image = up_move2;
                }
                break;
            case "right":
                if(spriteNumber == 1) {
                    image = right_move1;
                }
                if (spriteNumber == 2) {
                    image = right_move2;
                }
                break;
            case "left":
                if(spriteNumber == 1) {
                    image = left_move1;
                }
                if (spriteNumber == 2) {
                    image = left_move2;
                }
                break;
        }
        g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        g2.setColor(Color.RED);
        g2.drawRect(screenX + bounds.x, screenY + bounds.y, bounds.width, bounds.height);
    }
}