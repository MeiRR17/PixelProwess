package game.entity;

import main.data.DatabasePlayerLoader;
import main.data.PlayerLevelData;

public class CharacterStats {
    private final String characterName;
    private double speedMultiplier;
    private double healthMultiplier;
    private double attackMultiplier;
    private double criticalHitChanceMultiplier;
    private double defenseMultiplier;
    private int level;
    private final double levelMultiplier;

    public CharacterStats(String characterName, DatabasePlayerLoader loader) {
        this.characterName = characterName;

        // Load level data from database using both player name and character name
        PlayerLevelData levelData = loader.loadPlayerData("defaultPlayer", characterName); // Replace "defaultPlayer" with actual player name
        this.level = levelData.getLevel();
        this.levelMultiplier = levelData.getStatMultiplier();

        initializeStats();
    }

    private void initializeStats() {
        // Set base multipliers
        speedMultiplier = 1.0;
        healthMultiplier = 1.0;
        attackMultiplier = 1.0;
        criticalHitChanceMultiplier = 1.0;
        defenseMultiplier = 1.0;

        // Apply character-specific stat boosts
        switch (characterName.toLowerCase()) {
            case "pip" -> {
                speedMultiplier += 0.20;
                healthMultiplier += 0.10;
                attackMultiplier += 0.15;
                criticalHitChanceMultiplier += 0.05;
                defenseMultiplier += 0.05;
            }
            case "brock" -> {
                healthMultiplier += 0.25;
                defenseMultiplier += 0.20;
                attackMultiplier += 0.10;
                criticalHitChanceMultiplier += 0.05;
            }
            case "finn" -> {
                healthMultiplier += 0.15;
                attackMultiplier += 0.20;
                defenseMultiplier += 0.15;
                criticalHitChanceMultiplier += 0.10;
            }
            case "riley" -> {
                attackMultiplier += 0.15;
                defenseMultiplier += 0.15;
                healthMultiplier += 0.10;
                criticalHitChanceMultiplier += 0.05;
            }
        }

        // Apply level-based scaling
        speedMultiplier *= levelMultiplier;
        healthMultiplier *= levelMultiplier;
        attackMultiplier *= levelMultiplier;
        defenseMultiplier *= levelMultiplier;
        // Critical hit chance scales at half the rate
        criticalHitChanceMultiplier *= (1 + ((levelMultiplier - 1) * 0.5));
    }

    // Existing getters
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getHealthMultiplier() { return healthMultiplier; }
    public double getAttackMultiplier() { return attackMultiplier; }
    public double getCriticalHitChanceMultiplier() { return criticalHitChanceMultiplier; }
    public double getDefenseMultiplier() { return defenseMultiplier; }
    public String getCharacterName() { return characterName; }
    public int getLevel() { return level; }

    public void updateLevel(int level) {
        this.level++;
    }
}