package object.weapons;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public abstract class Weapon {
    protected String gunImagePath;  // Image used in-game
    protected String iconImagePath; // Image used as tile icon
    protected String bulletPath;
    public BufferedImage gunImage;
    public double scale;
    public BufferedImage iconImage;
    public BufferedImage bulletImage;
    public String weaponName;
    public boolean collision;
    public int worldX, worldY;
    public Rectangle bounds = new Rectangle(0 ,0 ,32 , 32);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    //Weapon states
    public int DAMAGE;
    public int MAGAZINE_SIZE;
    public double FIRE_RATE;
    public double RELOAD_TIME;

    public int currentAAmmo;
    public boolean isReloading;
    protected long reloadStartTime;

    public Weapon(String gunImagePath, double scale, String iconImagePath, String weaponName, String bulletPath, int DAMAGE, int MAGAZINE_SIZE, double FIRE_RATE, double RELOAD_TIME) throws IOException {
        this.gunImagePath = gunImagePath;
        this.scale = scale;
        this.iconImagePath = iconImagePath;
        this.weaponName = weaponName;
        this.bulletPath = bulletPath;
        this.gunImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(this.gunImagePath)));
        this.gunImage = resizeImage(ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(this.gunImagePath))), (int) this.scale * gunImage.getWidth(), (int) this.scale * gunImage.getHeight());
        this.iconImage = resizeImage(ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(iconImagePath))));
        this.bulletImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(this.bulletPath)));
        this.DAMAGE = DAMAGE;
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.currentAAmmo = MAGAZINE_SIZE;
        this.FIRE_RATE = FIRE_RATE;
        this.RELOAD_TIME = RELOAD_TIME;
        this.isReloading = false;
    }

    public void reload() {
        if(!isReloading && currentAAmmo < MAGAZINE_SIZE) {
            isReloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
    }

    public void updateReload() {
        if(isReloading) {
            long now = System.currentTimeMillis();
            if(now - reloadStartTime > RELOAD_TIME * 1000) {
                finishReload();
            }
        }
    }

    public void finishReload() {
        currentAAmmo = MAGAZINE_SIZE;
        isReloading = false;
    }

    public void draw(Graphics2D g, GamePanel gamePanel) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;
        if (worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY) {
            g.drawImage(iconImage, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
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

    protected BufferedImage resizeImage(BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}
