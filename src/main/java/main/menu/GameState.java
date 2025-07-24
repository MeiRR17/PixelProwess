package main.menu;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import main.data.DatabaseManager;
import main.data.DatabasePlayerLoader;
import main.data.FirebaseManager;
import game.entity.CharacterStats;
import main.manager.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameState {
    private static GameState instance;
    private final DatabaseManager dbManager;
    private final DatabasePlayerLoader dbLoader;
    private final FirebaseManager firebaseManager;
    private String playerName;
    private String selectedCharacter;
    private String currentUserId;
    private int playerLevel;
    private int currentScore;
    private GamePanel gamePanel;
    private PlayMenu playMenu;
    private Map<String, CharacterStats> preloadedStats;
    private boolean isAuthenticated;

    private SoundManager soundManager;

    public GameState() {
        this.dbManager = new DatabaseManager();
        this.dbLoader = new DatabasePlayerLoader(dbManager);
        try {
            this.firebaseManager = new FirebaseManager();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
        this.currentScore = 0;
        this.isAuthenticated = false;
        this.soundManager = new SoundManager();
        preloadCharacterStats();
    }


    public boolean authenticate(String username, String password) {
        if (firebaseManager.signIn(username, password)) {
            this.isAuthenticated = true;
            setCurrentUser(username, firebaseManager.getCurrentUserId());
            System.out.println("GameState currentUser set to: " + currentUserId);
            return true;
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (firebaseManager.signUp(username, password)) {
            setCurrentUser(username, firebaseManager.getCurrentUserId());
            System.out.println("GameState currentUser set to: " + currentUserId);
            return true;
        }
        return false;
    }

    public void restartGame() throws IOException {
        // Completely recreate the GamePanel for a fresh start
        if (gamePanel != null) {
            gamePanel.stopGameThread();
            gamePanel = null;  // Allow for garbage collection
        }

        // Ensure a character is selected
        if (selectedCharacter == null) {
            // Set a default character if none is selected
            selectedCharacter = "pip";
        }

        // Initialize a new game panel
        initializeGamePanel();

        // Reset current score
        currentScore = 0;
    }

    public void updateOnlineScore() {
        if (isAuthenticated && currentScore > 0) {
            firebaseManager.updateScore(currentScore);
        }
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void saveHighScore(String playerName, int highestScore) {
        String data = playerName + "," + highestScore;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("high_scores.txt", true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void preloadCharacterStats() {
        preloadedStats = new HashMap<>();
        // Preload stats for all characters in background
        CompletableFuture.runAsync(() -> {
            String[] characters = {"pip", "finn", "riley", "brock"};
            for (String character : characters) {
                preloadedStats.put(character, new CharacterStats(character, dbLoader));
            }
        });
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void signOut() {
        this.currentUserId = null;
        this.isAuthenticated = false;
        this.currentScore = 0;
    }

    // Existing Methods
    public void initializeGamePanel() throws IOException {
        if (gamePanel == null) {
            gamePanel = new GamePanel(this);
        }
    }

    public void initializePlayMenu(CardLayout cardLayout, JPanel cardPanel) {
        if (playMenu == null) {
            playMenu = new PlayMenu(this, cardLayout, cardPanel);
        }
    }

    public CharacterStats getPreloadedStats(String character) {
        return preloadedStats.get(character);
    }

    public DatabasePlayerLoader getDbLoader() {
        return dbLoader;
    }

    public void setSelectedCharacter(String character) {
        this.selectedCharacter = character;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public PlayMenu getPlayMenu() {
        return playMenu;
    }

    public void setPlayerLevel(int level) {
        this.playerLevel = level;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    // New Getters
    public int getCurrentScore() {
        return currentScore;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    public void setCurrentUser(String username, String userId) {
        this.currentUserId = userId;
        this.playerName = username;
        System.out.println("Current user set to: " + username + " with ID: " + userId);
    }
    public void updateOnlineScore(int score) {
        if (isAuthenticated && score > 0) {
            this.currentScore = score; // Update current score
            firebaseManager.updateScore(this.currentScore);
        }
    }
    public int getHighScore() {
        if (currentUserId == null) {
            return 0;
        }

        DocumentReference docRef = firebaseManager.getDb().collection("players").document(currentUserId);
        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                return document.getLong("highScore").intValue();
            } else {
                return 0;
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error retrieving high score: " + e.getMessage());
            return 0;
        }
    }
}
