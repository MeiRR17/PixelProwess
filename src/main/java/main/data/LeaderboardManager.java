package main.data;

import java.sql.*;

public class LeaderboardManager {
    private static final String DB_URL = "jdbc:sqlite:game.db";
    private String currentUser;

    public LeaderboardManager(String currentUser) {
        this.currentUser = currentUser;
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS scores (player_name TEXT PRIMARY KEY, score INTEGER NOT NULL)");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    public void updateScore(String playerName, int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO scores (player_name, score) VALUES (?, ?)");
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Score update failed: " + e.getMessage());
        }
    }

    public void displayLeaderboard() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM scores ORDER BY score DESC");

            while (rs.next()) {
                System.out.println(rs.getString("player_name") + ": " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to display leaderboard: " + e.getMessage());
        }
    }
}
