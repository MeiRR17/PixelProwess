package main.data;

public class MobLevelGenerator {
    public static int generateMobLevel(int playerLevel) {
        int maxLevel = playerLevel + 10;
        double chance = 1.0; // Starting chance

        // Keep trying levels until we succeed
        for (int level = playerLevel; level <= maxLevel; level++) {
            if (Math.random() < chance) {
                return level;
            }
            chance *= 0.9; // Reduce chance by 10% for next level
        }

        return playerLevel; // Fallback to player level if no level was selected
    }

    public static double getMobStatMultiplier(int mobLevel) {
        return 1 + (mobLevel * 0.06); // Same scaling as players: 6% per level
    }
}
