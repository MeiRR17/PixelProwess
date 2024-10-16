package object;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ObjMaster{
    public BufferedImage image;
    public String name;
    public boolean collision;
    public int worldX, worldY;
    public Rectangle bounds = new Rectangle(0 ,0 ,32 , 32);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public ObjMaster(BufferedImage image){
        this.image = resizeImage(image, 32, 32);
    }




    public void draw(Graphics2D g, GamePanel gamePanel) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;
        if (worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY) {
            g.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        }
    }

    protected BufferedImage resizeImage(BufferedImage image, int width, int height) {
        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());

        // Draw the original image to the resized image
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}
