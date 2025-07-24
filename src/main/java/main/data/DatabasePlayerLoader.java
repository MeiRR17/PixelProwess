package main.data;

public class DatabasePlayerLoader {
    private final DatabaseManager dbManager;

    public DatabasePlayerLoader(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public PlayerLevelData loadPlayerData(String playerName, String characterName) {
        return dbManager.getPlayerStats(playerName, characterName);
    }

    public void savePlayerProgress(String playerName, String characterName, int level) {
        dbManager.updateCharacterLevel(playerName, characterName, level);
    }

    public void updatePlayerLevel(String playerName, String characterName, int newLevel) {
        dbManager.updateCharacterLevel(playerName, characterName, newLevel);
    }

    public void updateHighScore(String playerName, int newScore) {
        dbManager.updateHighScore(playerName, newScore);
    }
}