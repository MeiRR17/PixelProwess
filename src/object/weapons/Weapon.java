package object.weapons;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public abstract class Weapon implements Cloneable {
    private static final Random random = new Random();
    protected String gunImagePath;  // Image used in-game
    protected String[] iconImagePath; // Image used as tile icon
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

    public String rarity;

    // Weapon states
    public int DAMAGE;
    public int MAGAZINE_SIZE;
    public double FIRE_RATE;
    public double RELOAD_TIME;

    public int ammoLeft;
    public boolean isReloading;
    protected long reloadStartTime;

    public Weapon(String gunImagePath, double scale, String[] iconImagePath, String weaponName, String bulletPath, int DAMAGE, int MAGAZINE_SIZE, double FIRE_RATE, double RELOAD_TIME) throws IOException {
        this.gunImagePath = gunImagePath;
        this.scale = scale;
        this.iconImagePath = iconImagePath;
        this.weaponName = weaponName;
        this.bulletPath = bulletPath;

        // Load images using the new loadImage method
        this.gunImage = loadImage(this.gunImagePath); // Load gun image first
        this.gunImage = resizeImage(this.gunImage, (int) this.scale * gunImage.getWidth(), (int) this.scale * gunImage.getHeight());
        this.iconImage = resizeImage(loadImage(iconImagePath[getRarity()]));
        this.bulletImage = loadImage(this.bulletPath);

        this.DAMAGE = DAMAGE;
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.ammoLeft = MAGAZINE_SIZE;
        this.FIRE_RATE = FIRE_RATE;
        this.RELOAD_TIME = RELOAD_TIME;

        // Adjust stats based on rarity
        adjustStatsBasedOnRarity();
        this.isReloading = false;
    }

    @Override
    public Weapon clone() throws CloneNotSupportedException {
        return (Weapon) super.clone(); // Call the superclass's clone method
    }

    public void reload() {
        if(!isReloading && ammoLeft < MAGAZINE_SIZE) {
            isReloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
    }

    public int getRarity() {
        int chance = random.nextInt(100); // Generate a random number between 0 and 99

        if (chance < 40) {
            rarity = "common";
            return 0; // 40% chance
        } else if (chance < 65) {
            rarity = "uncommon";
            return 1; // 30% chance
        } else if (chance < 85) {
            rarity = "rare";
            return 2; // 20% chance
        } else if (chance < 95) {
            rarity = "epic";
            return 3; // 10% chance
        } else {
            rarity = "legendary";
            return 4; // 5% chance
        }
    }

    private void adjustStatsBasedOnRarity() {
        switch (rarity) {
            case "common":
                // No changes for common
                break;
            case "uncommon":
                RELOAD_TIME *= 0.9; // Decrease reload time by 10%
                FIRE_RATE *= 1.1; // Increase fire rate by 10%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.1); // Increase magazine size by 10%
                break;
            case "rare":
                RELOAD_TIME *= 0.85; // Decrease reload time by 15%
                FIRE_RATE *= 1.2; // Increase fire rate by 20%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.2); // Increase magazine size by 20%
                break;
            case "epic":
                RELOAD_TIME *= 0.8; // Decrease reload time by 20%
                FIRE_RATE *= 1.3; // Increase fire rate by 30%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.3); // Increase magazine size by 30%
                break;
            case "legendary":
                RELOAD_TIME *= 0.7; // Decrease reload time by 30%
                FIRE_RATE *= 1.4; // Increase fire rate by 40%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.4); // Increase magazine size by 40%
                break;
            default:
                throw new IllegalArgumentException("Unknown rarity: " + rarity);
        }
    }

    protected BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path), "Image not found: " + path));
    }

    public void updateReload() {
        if (isReloading) {
            long now = System.currentTimeMillis();
            if (now - reloadStartTime > RELOAD_TIME * 1000) {
                finishReload();
            }
        }
    }



    public void finishReload() {
        // Only refill ammo if the weapon is not dropped
        if (ammoLeft < MAGAZINE_SIZE) {
            ammoLeft = MAGAZINE_SIZE; // Refill ammo to magazine size
        }
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