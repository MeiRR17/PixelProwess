package game.entity;

import main.menu.GamePanel;
import game.object.weapon.Chest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectPlacer {
    private final GamePanel gamePanel;
    private final Random random = new Random();
    public List<Chest> chests;
    private static final int CHEST_COUNT = 10;
    private static final double POTION_CHEST_CHANCE = 0.20;
    private static final double COMMON_CHEST_CHANCE = 0.40;
    private static final double RARE_CHEST_CHANCE = 0.30;
    public boolean objectsPlaced = false;  // Track if objects have been placed

    public ObjectPlacer(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.chests = new ArrayList<>();
    }

    public void placeObjects() throws IOException {
        placeChests();
    }


    private void placeChests() throws IOException {
        // Clear any existing chests
        chests.clear();
        for (int i = 0; i < CHEST_COUNT; i++) {
            int worldX, worldY;
            do {
                worldX = random.nextInt(gamePanel.worldColumn) * gamePanel.tileSize;
                worldY = random.nextInt(gamePanel.worldRow) * gamePanel.tileSize;
            } while (isCollision(worldX, worldY));

            String rarity = determineChestRarity();
            Chest chest = new Chest(gamePanel, rarity, worldX, worldY);
            chests.add(chest);
        }
        objectsPlaced = true;
    }
    private String determineChestRarity() {
        double chance = random.nextDouble();
        if (chance < POTION_CHEST_CHANCE) {
            return "potion";
        } else if (chance < POTION_CHEST_CHANCE + COMMON_CHEST_CHANCE) {
            return "common";
        } else if (chance < POTION_CHEST_CHANCE + COMMON_CHEST_CHANCE + RARE_CHEST_CHANCE) {
            return "rare";
        } else {
            return "legendary";
        }
    }

    private String getChestRarity() {
        int chance = random.nextInt(100);
        if (chance < 20) {
            return "potion";  // 20% chance for potion chest
        } else if (chance < 60) {
            return "common";  // 40% chance
        } else if (chance < 90) {
            return "rare";    // 30% chance
        } else {
            return "legendary"; // 10% chance
        }
    }

    private boolean isCollision(int worldX, int worldY) {
        int col = worldX / gamePanel.tileSize;
        int row = worldY / gamePanel.tileSize;

        // Check tile collisions
        if (gamePanel.map.tiles[gamePanel.map.mapNumber[col][row]].collision ||
                gamePanel.map.tiles[gamePanel.map.mapNumber[col][row]].customCollision) {
            return true;
        }

        // Check chest collisions
        return chests.stream().anyMatch(chest ->
                chest.worldX == worldX && chest.worldY == worldY);
    }
    // Helper method to find next available weapon slot
    public int getNextAvailableWeaponSlot() {
        for (int i = 0; i < gamePanel.weapons.length; i++) {
            if (gamePanel.weapons[i] == null) {
                return i;
            }
        }
        return -1;
    }
}

