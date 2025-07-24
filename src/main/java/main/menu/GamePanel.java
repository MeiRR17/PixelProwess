package main.menu;

import game.entity.GameOverHandler;
import game.entity.ObjectPlacer;
import game.entity.Player;
import main.data.DatabaseManager;
import main.data.DatabasePlayerLoader;
import main.data.FirebaseManager;
import game.entity.ui.InventoryDisplay;
import game.map.Collision;
import game.map.MapTransitionManager;
import game.object.mob.*;
import game.object.weapon.*;
import game.map.Map;
import game.utility.KeyHandler;
import game.utility.MouseHandler;
import main.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    public boolean debug = false; //debug mode flag

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int screenWidth = screenSize.width;
    public final int screenHeight = screenSize.height - 30;
    public ArrayList<Mob> mobs = new ArrayList<>(); // Update to BaseMob

    // Screen settings
    public final int orgTileSize = 32; // 32x32 tile
    final int scale = 2; // Scaling the tile to make it bigger
    public GameState gameState;  // Add this field

    public final int tileSize = orgTileSize * scale;

    public final int worldColumn = 100;
    public final int worldRow = 100;

    int FPS = 60;

    public KeyHandler keyHandler = new KeyHandler(gameState);
    Thread gameThread;
    public Collision collisionCheck = new Collision(this);
    public ObjectPlacer objectPlacer = new ObjectPlacer(this);
    public Player player;
    public Weapon[] weapons = new Weapon[25];
    public Map map;
    public InventoryDisplay inventoryDisplay;
    public ScoreManager scoreManager;
    public MapTransitionManager mapTransitionManager;
    private static final int MIN_SPAWN_DISTANCE = 800; // Pixels away from player
    private static final int MAX_SPAWN_DISTANCE = 1200; // Maximum spawn distance
    private static final int MAX_MOBS = 8; // Maximum number of mobs allowed at once
    private static final int SPAWN_INTERVAL = 5000; // 5 seconds between spawn attempts
    private long lastSpawnTime = 0;
    private final Random random = new Random(); // Add random for mob type selection
    private final FirebaseManager firebaseManager;
    private GameOverHandler gameOverHandler;

    public GamePanel(GameState gameState) throws IOException {
        this.gameState = gameState;
        DatabaseManager dbManager = new DatabaseManager();
        DatabasePlayerLoader dbLoader = gameState.getDbLoader();

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        MouseHandler mouseHandler = new MouseHandler(player);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(this);

        player = new Player(this, keyHandler, mouseHandler, dbLoader);
        map = new Map(this);  // Initialize map first
        this.mapTransitionManager = new MapTransitionManager(this);  // Then initialize transition manager
        mapTransitionManager.loadInitialMap();  // Then load initial map

        objectPlacer.chests = new ArrayList<>();
        inventoryDisplay = new InventoryDisplay(screenWidth, screenHeight, player);
        this.scoreManager = new ScoreManager(this, gameState);
        firebaseManager = new FirebaseManager();
        this.gameOverHandler = new GameOverHandler(this, player);
    }
    public void endGame(int finalScore) {
        // Block immediate transitions - force a delay
        System.out.println("GamePanel.endGame called - adding forced delay");

        // Save the score in a separate thread to avoid blocking UI
        new Thread(() -> {
            System.out.println("Updating score in background");
            gameState.getFirebaseManager().updateScore(finalScore);
        }).start();

        // Force a 5-second delay before transitioning
        new Thread(() -> {
            try {
                System.out.println("GAME PANEL: Starting 5-second forced delay");
                Thread.sleep(5000);
                System.out.println("GAME PANEL: 5-second delay completed");

                // After delay, return to menu
                SwingUtilities.invokeLater(() -> {
                    System.out.println("GAME PANEL: Stopping game thread");
                    stopGameThread();

                    System.out.println("GAME PANEL: Resetting game");
                    resetGame();

                    System.out.println("GAME PANEL: Transitioning to play menu");
                    if (gameState != null && gameState.getPlayMenu() != null) {
                        CardLayout cardLayout = (CardLayout) gameState.getPlayMenu().getParent().getLayout();
                        cardLayout.show(gameState.getPlayMenu().getParent(), "PlayMenu");
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void resetGame() {
        // Clear all game objects
        mobs.clear();
        if (objectPlacer != null && objectPlacer.chests != null) {
            objectPlacer.chests.clear();
        }

        // Clear bullets
        if (Player.bullets != null) {
            Player.bullets.clear();
        }

        // Reset object placement flag
        if (objectPlacer != null) {
            objectPlacer.objectsPlaced = false;
        }

        // Reset weapons array
        for (int i = 0; i < weapons.length; i++) {
            weapons[i] = null;
        }

        // Reset score
        if (scoreManager != null) {
            scoreManager.resetScore();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        map.draw(g2);

        if (debug) {
            g2.setColor(Color.RED);
            for (Bullet bullet : Player.bullets) {
                // Adjust bullet drawing to be relative to player's position
                Rectangle bulletBox = new Rectangle(
                        bullet.x - player.playerX + player.screenX,
                        bullet.y - player.playerY + player.screenY,
                        10,
                        5
                );
                g2.draw(bulletBox);
            }

            // Draw mob collision boxes
            g2.setColor(Color.BLUE);
            for (Mob mob : mobs) {
                Rectangle mobBox = new Rectangle(
                        mob.worldX - player.playerX + player.screenX,
                        mob.worldY - player.playerY + player.screenY,
                        mob.MOB_WIDTH,
                        mob.MOB_HEIGHT
                );
                g2.draw(mobBox);
            }

            // Add debug info
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 20));

            // Calculate current tile position
            int currentTileX = player.playerX / tileSize;
            int currentTileY = player.playerY / tileSize;

            // Get current map name from the path
            String currentMapPath = map.getCurrentMapPath();
            String mapName = currentMapPath.substring(currentMapPath.lastIndexOf("/") + 1)
                    .replace(".txt", "")
                    .toUpperCase();

            // Create combined debug string
            String debugInfo = String.format("Map: %s | Tile Position: (%d, %d)",
                    mapName, currentTileX, currentTileY);

            // Position and draw the debug text
            int padding = 20;
            g2.drawString(debugInfo, padding, padding + g2.getFontMetrics().getHeight());
        }
        for (Weapon weapon : weapons) {
            if (weapon != null) {
                weapon.draw(g2, this);
            }
        }

        // Create a copy of the mobs list for safe iteration
        List<Mob> mobsCopy = new ArrayList<>(mobs);
        for (Mob mob : mobsCopy) {
            if (mob != null) {
                mob.draw(g2);
            }
        }

        // Drawing chests from objectPlacer.chests
        List<Chest> chestsCopy = new ArrayList<>(objectPlacer.chests);
        for (Chest chest : chestsCopy) {
            chest.draw(g2);
        }

        player.draw(g2);
        drawBulletsLeft(g2, player.currentWeapon);

        inventoryDisplay.draw(g2);
        scoreManager.draw(g2);

        g2.dispose();
    }
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                try {
                    update();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                repaint();
                delta--;
            }
        }
    }

    public void update() throws IOException {
        mapTransitionManager.checkForTransition();
        long startTime = System.nanoTime();

        // Check for game over condition first
        if (player.health <= 0 && !gameOverHandler.isGameOver()) {
            gameOverHandler.checkGameOver();
        }

        // Update game over handler if active
        if (gameOverHandler.isGameOver()) {
            gameOverHandler.update();

            // Even in game over state, continue updating mobs for their death animations
            for (int i = mobs.size() - 1; i >= 0; i--) {
                Mob mob = mobs.get(i);
                if (mob != null) {
                    // If in game over mode, ensure all mobs are dying or dead
                    if (!mob.isDead && !mob.currentState.equals("death")) {
                        mob.takeDamage(mob.getHealth()); // Ensure the mob is dying
                    }

                    mob.update(); // Continue updating for animations

                    // Remove mobs that have completed their death animation
                    if (mob.isDead && mob.isDeathAnimationComplete()) {
                        // Don't add score during game over sequence since we're already dead
                        mobs.remove(i);
                    }
                }
            }

            // Update player for floating animation
            player.update();

            return; // Exit early, no need for other updates during game over
        }

        // Rest of your normal update code remains the same
        if (player != null) {
            player.update();
        }
        scoreManager.update();

        if (!player.isAlive()) {
            int finalScore = scoreManager.getCurrentScore();
            endGame(finalScore);
        }

        // Check for game over condition
        if (player.health <= 0 && !gameOverHandler.isGameOver()) {
            gameOverHandler.checkGameOver();
        }

        // Update game over handler if active
        if (gameOverHandler.isGameOver()) {
            gameOverHandler.update();
        }
        // Check if we're in a non-base map
        String currentMap = map.getCurrentMapPath();
        boolean isBaseMap = currentMap != null && currentMap.equals("/maps/base.txt");

        if (!isBaseMap) {
            // Place objects (chests) if not already placed
            if (!objectPlacer.objectsPlaced) {
                objectPlacer.placeObjects();
                objectPlacer.objectsPlaced = true;
            }

            // Handle mob spawning
            handleMobSpawning();
        } else {
            // Clear mobs and chests when in base map
            mobs.clear();
            objectPlacer.chests.clear();
            objectPlacer.objectsPlaced = false;
        }
        // Update mobs
        for (int i = mobs.size() - 1; i >= 0; i--) {
            Mob mob = mobs.get(i);
            if (mob != null) {
                mob.update();
            }
            // Only remove after full death animation
            if (mob.isDead && mob.currentState.equals("death") && mob.currentFrame >= 7) {
                player.gainExperience(mob);
                scoreManager.addScore(mob.getScoreValue());
                mobs.remove(i);
            }
        }

        // Bullet collision with mobs
        for (int i = Player.bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = Player.bullets.get(i);
            if (bullet != null) {
                Rectangle bulletBox = new Rectangle(
                        bullet.x,
                        bullet.y,
                        10,  // Bullet width
                        5    // Bullet height
                );

                // Mob collision check
                for (int j = mobs.size() - 1; j >= 0; j--) {
                    Mob mob = mobs.get(j);
                    if (mob != null) {
                        Rectangle mobBox = mob.getWorldCollisionBox();

                        if (debug) {
                            System.out.println("Bullet World Coords: (" + bullet.x + ", " + bullet.y + ")");
                            System.out.println("Mob World Coords: (" + mob.worldX + ", " + mob.worldY + ")");
                            System.out.println("Bullet Box: " + bulletBox);
                            System.out.println("Mob Box: " + mobBox);
                        }

                        if (mobBox != null && bulletBox.intersects(mobBox)) {
                            mob.takeDamage(bullet.damage);
                            Player.bullets.remove(i);
                            break;
                        }
                    }
                }
            }
            // Check for spawning new mobs
// Check for spawning new mobs
            handleMobSpawning();
            // Update score
            if (scoreManager != null) {
                scoreManager.update();
            }
        }

        // Add weapon pickup logic
        for (int i = 0; i < weapons.length; i++) {
            Weapon weapon = weapons[i];
            if (weapon != null) {
                int distance = (int) Math.sqrt(
                        Math.pow(player.playerX - weapon.worldX, 2) +
                                Math.pow(player.playerY - weapon.worldY, 2)
                );

                if (distance < tileSize && keyHandler.isEPressed) {
                    if (weapon instanceof Big) {
                        if (player.bigWeapon != null) {
                            player.dropWeapon(player.bigWeapon);
                        }
                        player.bigWeapon = (Big) weapon;
                        player.currentWeapon = player.bigWeapon;
                    } else if (weapon instanceof Small) {
                        if (player.smallWeapon != null) {
                            player.dropWeapon(player.smallWeapon);
                        }
                        player.smallWeapon = (Small) weapon;
                        player.currentWeapon = player.smallWeapon;
                    }

                    if (player.currentWeapon != null) {
                        player.currentBullet = player.currentWeapon.bulletImage;
                    }

                    weapons[i] = null;
                    keyHandler.isEPressed = false;
                    inventoryDisplay.updateDisplay();
                }
            }
        }

        // Check chest interactions
        for (Chest chest : objectPlacer.chests) {
            if (chest.isWithinRange(player.playerX, player.playerY, 32) && keyHandler.isEPressed) {
                chest.open();
            }
        }

        // Performance tracking
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        if (duration > 16) { // If update takes more than a frame (at 60 FPS)
            System.out.println("Performance warning: Update took " + duration + "ms");
        }
        //Reduce path recalculation frequency
        //Use object pooling for bullets and mobs
        //Optimize collision checks
        //Avoid creating new lists in update methods
        //Profile with Java profiling tools
    }

    public void gameSet() throws IOException {
        objectPlacer.placeObjects(); // Ensure objects are placed in objectPlacer.chests
    }

    public void startGameThread() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public int getNextAvailableWeaponIndex() {
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] == null) {
                return i; // Return the first available index
            }
        }
        return -1; // Return -1 if no available index is found
    }



    private void drawBulletsLeft(Graphics2D g2, Weapon currentWeapon) {
        if (currentWeapon != null) {
            String bulletsLeft = "Bullets: " + currentWeapon.ammoLeft + "/" + currentWeapon.MAGAZINE_SIZE;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Adjust font size here
            FontMetrics metrics = g2.getFontMetrics();
            int messageWidth = metrics.stringWidth(bulletsLeft);
            int messageX = screenWidth - messageWidth - 10; // Position from the right
            int messageY = screenHeight - 10; // Position from the bottom

            g2.drawString(bulletsLeft, messageX, messageY);
        }
    }

    private Point getValidSpawnPoint() {
        for (int attempts = 0; attempts < 50; attempts++) {
            double angle = Math.random() * 2 * Math.PI;
            double distance = MIN_SPAWN_DISTANCE + Math.random() * (MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE);

            int spawnX = (int) (player.playerX + Math.cos(angle) * distance);
            int spawnY = (int) (player.playerY + Math.sin(angle) * distance);

            if (isValidSpawnLocation(spawnX, spawnY)) {
                return new Point(spawnX, spawnY);
            }
        }
        return null;
    }

    private boolean isValidSpawnLocation(int x, int y) {
        // Check world bounds
        if (x < 0 || x >= worldColumn * tileSize || y < 0 || y >= worldRow * tileSize) {
            return false;
        }

        // Check tile collision
        int tileCol = x / tileSize;
        int tileRow = y / tileSize;

        // Add bounds checking to prevent ArrayIndexOutOfBounds
        if (tileCol >= worldColumn || tileRow >= worldRow) {
            return false;
        }

        int tileNum = map.mapNumber[tileCol][tileRow];
        if (map.tiles[tileNum].collision || map.tiles[tileNum].customCollision) {
            return false;
        }

        // Check if spawn point would be visible on screen
        int screenX = x - player.playerX + player.screenX;
        int screenY = y - player.playerY + player.screenY;
        if (screenX >= -tileSize && screenX <= screenWidth + tileSize &&
                screenY >= -tileSize && screenY <= screenHeight + tileSize) {
            return false;
        }

        return true;
    }

    private void handleMobSpawning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime < SPAWN_INTERVAL) {
            return;
        }

        if (mobs.size() < MAX_MOBS) {
            Point spawnPoint = getValidSpawnPoint();
            if (spawnPoint != null) {
                spawnMob(spawnPoint.x, spawnPoint.y);
                lastSpawnTime = currentTime;
            }
        }
    }
    private void spawnMob(int x, int y) {
        int playerLevel = player.characterStats.getLevel();
        Mob newMob = null;

        int mobType = random.nextInt(4);
        switch (mobType) {
            case 0 -> newMob = new Goblin(this, player, map, playerLevel);
            case 1 -> newMob = new Orc(this, player, map, playerLevel);
            case 2 -> newMob = new Uruk(this, player, map, playerLevel);
            case 3 -> newMob = new Satan(this, player, map, playerLevel);
        }

        if (newMob != null) {
            newMob.worldX = x;
            newMob.worldY = y;
            mobs.add(newMob);

            if (debug) {
                System.out.println("Spawned mob at: (" + x + ", " + y + ")");
            }
        }
    }


    public void stopGameThread() {
        gameThread = null;
    }

    // Mouse motion listener methods
    @Override
    public void mouseDragged(MouseEvent e) {
        // Update player angle while dragging
        player.updateAngle(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Update player angle on mouse move
        player.updateAngle(e.getX(), e.getY());
    }

}
