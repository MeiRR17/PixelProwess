package main.data;

public class PlayerLevelData {
    private final int level;
    private final double statMultiplier;
    private final int highScore;

    public PlayerLevelData(int level, double statMultiplier, int highScore) {
        this.level = level;
        this.statMultiplier = statMultiplier;
        this.highScore = highScore;
    }

    public int getLevel() { return level; }
    public double getStatMultiplier() { return statMultiplier; }
    public int getHighScore() { return highScore; }
}