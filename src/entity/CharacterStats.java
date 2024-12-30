package entity;

public class CharacterStats {
    private final String characterName;
    private double speedMultiplier;
    private double healthMultiplier;
    private double attackMultiplier;
    private double criticalHitChanceMultiplier;
    private double defenseMultiplier;

    public CharacterStats(String characterName) {
        this.characterName = characterName;
        initializeStats();
    }

    private void initializeStats() {
        // Set base multipliers to 1.0 (100%)
        speedMultiplier = 1.0;
        healthMultiplier = 1.0;
        attackMultiplier = 1.0;
        criticalHitChanceMultiplier = 1.0;
        defenseMultiplier = 1.0;

        // Apply character-specific stat boosts
        switch (characterName.toLowerCase()) {
            case "pip" -> {
                speedMultiplier += 0.20;      // +20% Speed
                healthMultiplier += 0.10;      // +10% Health
                attackMultiplier += 0.15;      // +15% Attack
                criticalHitChanceMultiplier += 0.05;  // +5% Crit
                defenseMultiplier += 0.05;     // +5% Defense
            }
            case "brock" -> {
                healthMultiplier += 0.25;      // +25% Health
                defenseMultiplier += 0.20;     // +20% Defense
                attackMultiplier += 0.10;      // +10% Attack
                criticalHitChanceMultiplier += 0.05;  // +5% Crit
            }
            case "finn" -> {
                healthMultiplier += 0.15;      // +15% Health
                attackMultiplier += 0.20;      // +20% Attack
                defenseMultiplier += 0.15;     // +15% Defense
                criticalHitChanceMultiplier += 0.10;  // +10% Crit
            }
            case "riley" -> {
                attackMultiplier += 0.15;      // +15% Attack
                defenseMultiplier += 0.15;     // +15% Defense
                healthMultiplier += 0.10;      // +10% Health
                criticalHitChanceMultiplier += 0.05;  // +5% Crit
            }
        }
    }

    // Getters for the multipliers
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getHealthMultiplier() { return healthMultiplier; }
    public double getAttackMultiplier() { return attackMultiplier; }
    public double getCriticalHitChanceMultiplier() { return criticalHitChanceMultiplier; }
    public double getDefenseMultiplier() { return defenseMultiplier; }
    public String getCharacterName() { return characterName; }
}
