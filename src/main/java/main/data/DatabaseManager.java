package main.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private final String url = "jdbc:sqlite:game.db";

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");  // Load SQLite driver
            if (testConnection()) {            // Test if we can connect
                createTables();                // If yes, create tables
            } else {
                throw new RuntimeException("Could not establish database connection");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC Driver not found", e);
        }
    }

    private void createTables() {
        // Drop existing tables if they exist
        String dropCharactersSql = "DROP TABLE IF EXISTS CharacterLevels";
        String dropPlayersSql = "DROP TABLE IF EXISTS Players";

        // Create table for character-specific levels
        String createCharactersSql = "CREATE TABLE CharacterLevels (\n"
                + " player_name TEXT,\n"
                + " character_name TEXT,\n"
                + " level INTEGER DEFAULT 1\n"
                + ");";

        // Create table for player's high score
        String createPlayersSql = "CREATE TABLE Players (\n"
                + " player_name TEXT UNIQUE,\n"
                + " high_score INTEGER DEFAULT 0\n"
                + ");";

        try (Connection conn = this.connect()) {
            try (PreparedStatement dropCharsStmt = conn.prepareStatement(dropCharactersSql);
                 PreparedStatement dropPlayersStmt = conn.prepareStatement(dropPlayersSql);
                 PreparedStatement createCharsStmt = conn.prepareStatement(createCharactersSql);
                 PreparedStatement createPlayersStmt = conn.prepareStatement(createPlayersSql)) {

                dropCharsStmt.executeUpdate();
                dropPlayersStmt.executeUpdate();
                createCharsStmt.executeUpdate();
                createPlayersStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public Connection connect() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    private boolean testConnection() {
        try (Connection conn = connect()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void updateCharacterLevel(String playerName, String characterName, int newLevel) {
        String sql = "UPDATE CharacterLevels SET level = ? " +
                "WHERE player_name = ? AND character_name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newLevel);
            pstmt.setString(2, playerName);
            pstmt.setString(3, characterName);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // Character doesn't exist for this player, insert new record
                sql = "INSERT INTO CharacterLevels (player_name, character_name, level) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                    insertStmt.setString(1, playerName);
                    insertStmt.setString(2, characterName);
                    insertStmt.setString(3, String.valueOf(newLevel));
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating character level: " + e.getMessage());
        }
    }

    public void updateHighScore(String playerName, int newScore) {
        String sql = "INSERT INTO Players (player_name, high_score) VALUES (?, ?) " +
                "ON CONFLICT(player_name) DO UPDATE SET " +
                "high_score = CASE WHEN Players.high_score < ? THEN ? ELSE Players.high_score END";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setInt(2, newScore);
            pstmt.setInt(3, newScore);
            pstmt.setInt(4, newScore);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating high score: " + e.getMessage());
        }
    }

    public PlayerLevelData getPlayerStats(String playerName, String characterName) {
        String sql = "SELECT c.level, p.high_score " +
                "FROM CharacterLevels c " +
                "LEFT JOIN Players p ON c.player_name = p.player_name " +
                "WHERE c.player_name = ? AND c.character_name = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, characterName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int level = rs.getInt("level");
                int highScore = rs.getInt("high_score");
                double statMultiplier = 1 + (level * 0.06); // 6% increase per level
                return new PlayerLevelData(level, statMultiplier, highScore);
            } else {
                // Check if player has a high score but no character data yet
                sql = "SELECT high_score FROM Players WHERE player_name = ?";
                try (PreparedStatement hsStmt = conn.prepareStatement(sql)) {
                    hsStmt.setString(1, playerName);
                    ResultSet hsRs = hsStmt.executeQuery();
                    int highScore = hsRs.next() ? hsRs.getInt("high_score") : 0;
                    return new PlayerLevelData(1, 1.0, highScore);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting player stats: " + e.getMessage());
        }

        return new PlayerLevelData(1, 1.0, 0); // Default values for new players
    }
}
