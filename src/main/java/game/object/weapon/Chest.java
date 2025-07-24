package game.object.weapon;

import game.entity.ui.UIManager;
import game.object.weapon.weapons.*;
import main.menu.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Chest {
    private static final Random random = new Random();
    private final GamePanel gamePanel;
    private final String rarity;
    public int worldX, worldY;
    private BufferedImage imageClosed, imageOpen1, imageOpen2;
    public boolean isOpen;
    private Weapon containedWeapon;
    public Rectangle bounds;
    private int animationState = 0;
    private long animationStartTime;
    private static final long ANIMATION_FRAME_DURATION = 150;
    private Potion containedPotion;
    private boolean isPotionChest;
    private static final long WEAPON_SPAWN_DELAY = 500; // 0.5 seconds
    private static final long CHEST_DISAPPEAR_TIME = 200; // 1 second after opening
    private Timer disappearTimer;


    public Chest(GamePanel gamePanel, String rarity, int worldX, int worldY) throws IOException {
        this.gamePanel = gamePanel;
        this.rarity = rarity;
        this.worldX = worldX;
        this.worldY = worldY;
        this.isOpen = false;
        this.bounds = new Rectangle(0, 0, 32, 32);
        this.isPotionChest = rarity.equals("potion");

        // Load chest images with proper path handling
        loadChestImages();

        // Generate contents based on chest type
        if (isPotionChest) {
            generatePotion();
        } else {
            generateWeapon();
        }
    }

    private void loadChestImages() throws IOException {
        String basePath;
        if (isPotionChest) {
            basePath = "tiles/newTiles/place/newChest/potion/";
        } else {
            basePath = "tiles/newTiles/place/newChest/" + rarity + "/";
        }

        try {
            imageClosed = loadChestImage(basePath + "closed.png");
            imageOpen1 = loadChestImage(basePath + "open1.png");
            imageOpen2 = loadChestImage(basePath + "open2.png");
        } catch (IOException e) {
            System.err.println("Failed to load chest images from path: " + basePath);
            throw e;
        }
    }
    private BufferedImage loadChestImage(String path) throws IOException {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path),
                    "Chest image not found at path: " + path));
        } catch (NullPointerException e) {
            System.err.println("Failed to load chest image: " + path);
            throw new IOException("Failed to load chest image: " + path, e);
        }
    }
    private void generatePotion() throws IOException {
        int chance = random.nextInt(100);
        String potionType;

        // Ensure different potion types can be generated
        if (chance < 45) {
            potionType = "health";
        } else if (chance < 90) {
            potionType = "shield";
        } else {
            potionType = "ulti";
        }

        containedPotion = new Potion(potionType);
    }



    private void generateWeapon() throws IOException {
        int weaponType = random.nextInt(6);
        Weapon weapon = switch (weaponType) {
            case 0 -> new TacticalAssaultRifle();
            case 1 -> new Shotgun();
            case 2 -> new Pistol();
            case 3 -> new AK();
            case 4 -> new P90();
            case 5 -> new Scar();
            default -> throw new IllegalStateException("Unexpected weapon type: " + weaponType);
        };

        weapon.rarity = getWeaponRarity();
        weapon.adjustStatsBasedOnRarity();
        containedWeapon = weapon;
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
            animationState = 1;
            animationStartTime = System.currentTimeMillis();

            // Schedule chest disappearance
            disappearTimer = new Timer();
            disappearTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handleChestContents();
                    // Remove chest from the game
                    gamePanel.objectPlacer.chests.remove(Chest.this);
                }
            }, CHEST_DISAPPEAR_TIME);
        }
    }
    private void handleChestContents() {
        if (isPotionChest && containedPotion != null) {
            gamePanel.player.addPotion(containedPotion);
            containedPotion = null;
        } else if (containedWeapon != null) {
            spawnWeapon();
        }
    }
    private void spawnWeapon() {
        if (containedWeapon != null) {
            containedWeapon.worldX = worldX;
            containedWeapon.worldY = worldY;
            containedWeapon.updateBounds();

            int index = gamePanel.getNextAvailableWeaponIndex();
            if (index != -1) {
                gamePanel.weapons[index] = containedWeapon;
                containedWeapon = null;
            }
        }
    }

    public boolean isWithinRange(int playerX, int playerY, int range) {
        return Math.sqrt(Math.pow(playerX - worldX, 2) + Math.pow(playerY - worldY, 2)) <= range;
    }

    private void updateAnimation() {
        if (animationState > 0) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - animationStartTime;

            if (elapsed > ANIMATION_FRAME_DURATION * 2) {
                isOpen = true;
                animationState = 0;
            } else if (elapsed > ANIMATION_FRAME_DURATION) {
                animationState = 2;
            }
        }
    }

    public void draw(Graphics2D g2) {
        updateAnimation();
        if (isWithinScreen()) {
            drawChestSprite(g2);
            if (!isOpen && isWithinRange(gamePanel.player.playerX, gamePanel.player.playerY, gamePanel.tileSize)) {
                String promptText = "Press E to open " + (isPotionChest ? "potion chest" : rarity + " chest");
                UIManager.setPrompt(promptText);
            }
        }
    }
    private boolean isWithinScreen() {
        return worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY;
    }

    private void drawChestSprite(Graphics2D g2) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;

        BufferedImage imageToDraw;
        switch (animationState) {
            case 1 -> imageToDraw = imageOpen1;
            case 2 -> imageToDraw = imageOpen2;
            default -> imageToDraw = isOpen ? imageOpen2 : imageClosed;
        }

        g2.drawImage(imageToDraw, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
    }
}