package object.chests;

import main.GamePanel;
import object.weapons.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Chest {
    private static final Random random = new Random();
    private final GamePanel gamePanel;
    private final String rarity;
    public int worldX, worldY;
    private BufferedImage image;
    private boolean isOpen;
    private Weapon containedWeapon;
    public Rectangle bounds;

    public Chest(GamePanel gamePanel, String rarity, int worldX, int worldY) throws IOException {
        this.gamePanel = gamePanel;
        this.rarity = rarity;
        this.worldX = worldX;
        this.worldY = worldY;
        this.isOpen = false;
        this.bounds = new Rectangle(0, 0, 32, 32);

        // Load chest image based on rarity
        String imagePath = "tiles/newTiles/place/newChest/" + rarity + "/closed.png";
        try {
            this.image = ImageIO.read(
                    Objects.requireNonNull(getClass().getClassLoader().getResource(imagePath),
                            "Chest image not found at path: " + imagePath)
            );
        } catch (NullPointerException e) {
            throw new IOException("Failed to load chest image. Ensure the file exists at: " + imagePath, e);
        }

        // Generate weapon based on chest rarity when created
        generateWeapon();
    }

    private void generateWeapon() throws IOException {
        int weaponType = random.nextInt(6);
        Weapon weapon = null;
        switch (weaponType) {
            case 0 -> weapon = new TacticalAssaultRifle();
            case 1 -> weapon = new Shotgun();
            case 2 -> weapon = new Pistol();
            case 3 -> weapon = new AK();
            case 4 -> weapon = new P90();
            case 5 -> weapon = new Scar();
        }

        if (weapon != null) {
            weapon.rarity = getWeaponRarity();
            weapon.adjustStatsBasedOnRarity();
            containedWeapon = weapon;
        }
    }

    private String getWeaponRarity() {
        int chance = random.nextInt(100);

        return switch (this.rarity) {
            case "common" -> {
                if (chance < 50) yield "common";
                else if (chance < 70) yield "uncommon";
                else if (chance < 90) yield "rare";
                else if (chance < 95) yield "epic";
                else yield "legendary";
            }
            case "rare" -> {
                if (chance < 20) yield "uncommon";
                else if (chance < 60) yield "rare";
                else if (chance < 80) yield "epic";
                else yield "legendary";
            }
            case "legendary" -> {
                if (chance < 10) yield "rare";
                else if (chance < 50) yield "epic";
                else yield "legendary";
            }
            default -> "common";
        };
    }

    public void open() {
        if (!isOpen) {
            isOpen = true;
            if (containedWeapon != null) {
                containedWeapon.worldX = this.worldX;
                containedWeapon.worldY = this.worldY;
                int index = gamePanel.getNextAvailableWeaponIndex();
                if (index != -1) {
                    gamePanel.weapons[index] = containedWeapon;
                }
            }
        }
    }


    public boolean isWithinRange(int playerX, int playerY, int range) {
        return Math.sqrt(Math.pow(playerX - worldX, 2) + Math.pow(playerY - worldY, 2)) <= range;
    }

    public void update() {
        // Check if the player is within range and the "E" key is pressed
        if (isWithinRange(gamePanel.player.playerX, gamePanel.player.playerY, 32) && gamePanel.keyHandler.isEPressed) {
            open();
        }
    }

    public void draw(Graphics2D g2) {
        if (!isOpen) {
            int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;

            if (worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                    worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                    worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                    worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY) {
                g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
                if (isWithinRange(gamePanel.player.playerX, gamePanel.player.playerY, 32)) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    g2.drawString("Press E to open", screenX, screenY - 10);
                }
            }
        }
    }
}