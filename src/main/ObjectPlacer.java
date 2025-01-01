package main;

import object.chests.Chest;
import object.weapons.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectPlacer {
    private final GamePanel gamePanel;
    private final Random random = new Random();
    public List<Chest> chests;

    public ObjectPlacer(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.chests = new ArrayList<>();
    }

    public void placeObjects() throws IOException {
        // Place existing weapons
        placeWeapons();
        // Place chests
        placeChests();
    }

    private void placeWeapons() throws IOException {
        // Your existing weapon placement code...
    }

    private void placeChests() throws IOException {
        for (int i = 0; i < 10; i++) {
            // Find a valid position (collision = false)
            int worldX, worldY;
            do {
                worldX = random.nextInt(gamePanel.worldColumn) * gamePanel.tileSize;
                worldY = random.nextInt(gamePanel.worldRow) * gamePanel.tileSize;
            } while (isCollision(worldX, worldY));

            // Determine chest rarity
            String rarity = getChestRarity();

            // Create and add chest
            Chest chest = new Chest(gamePanel, rarity, worldX, worldY);
            chests.add(chest);
        }
    }

    private String getChestRarity() {
        int chance = random.nextInt(100);
        if (chance < 60) return "common";
        else if (chance < 90) return "rare";
        else return "legendary";
    }

    private boolean isCollision(int worldX, int worldY) {
        int col = worldX / gamePanel.tileSize;
        int row = worldY / gamePanel.tileSize;
        return gamePanel.tileManager.tiles[gamePanel.tileManager.mapNumber[col][row]].collision;
    }

}

