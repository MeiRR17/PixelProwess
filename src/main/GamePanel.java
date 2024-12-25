package main;

import entity.Player;
import object.bullets.Bullet;
import object.weapons.Weapon;
import tile.TileManager;
import utility.KeyHandler;
import utility.MouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable, MouseMotionListener {

    // Screen settings
    public final int orgTileSize = 32; // 32x32 tile
    final int scale = 2; // Scaling the tile to make it bigger

    public final int tileSize = orgTileSize * scale;
    public final int maxScreenColumn = 30;
    public final int maxScreenRow = 18;
    private static final Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // Gets the size of user's window

    public final int screenWidth = size.width;
    public final int screenHeight = size.height;

    public final int worldColumn = 100;
    public final int worldRow = 100;
//    private int something = Toolkit.getDefaultToolkit().getScreenResolution();

    private int frameCount = 0;
    private int currentFPS = 0;
    private long lastTime = System.nanoTime();

    private boolean isFullscreen = false; // Track whether we are in fullscreen
    private JFrame frame; // Reference to the JFrame

    int FPS = 60;
    TileManager tileManager = new TileManager(this);

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    public Collision collisionCheck = new Collision(this);
    public ObjectPlacer objectPlacer = new ObjectPlacer(this);
    public Player player = new Player(this, keyHandler, null);
    public Weapon[] weapons = new Weapon[25];


    public GamePanel(JFrame frame) throws IOException {
        this.frame = frame; // Assign the JFrame reference
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        MouseHandler mouseHandler = new MouseHandler(player);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(this);
        player = new Player(this, keyHandler, mouseHandler);

        // Add a key listener to toggle fullscreen mode using Alt + Enter
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.isAltDown()) { // Press 'Alt + Enter' to toggle fullscreen
                    toggleFullScreen();
                }
            }
        });
    }

    private void toggleFullScreen() {
        if (isFullscreen) {
            // Switch to windowed mode
            frame.dispose(); // Dispose of the current frame
            frame.setUndecorated(false); // Remove fullscreen decorations
            frame.setSize(800, 600); // Set your desired window size
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true); // Show the window
        } else {
            // Switch to fullscreen mode
            frame.dispose(); // Dispose of the current frame
            frame.setUndecorated(true); // Remove window decorations
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the frame
            frame.setVisible(true); // Show the frame
        }
        isFullscreen = !isFullscreen; // Toggle the state
    }
    public GamePanel(JFrame frame) throws IOException {
        this.frame = frame;
        this.setPreferredSize(new Dimension(800, 600)); // Set a fixed windowed resolution
        this.setDoubleBuffered(true); // For smoother rendering
        this.setFocusable(true);
        this.addMouseMotionListener(this);
        this.addKeyListener(keyHandler);
        isFullscreen = false; // Ensure fullscreen is disabled
    }

//    private void toggleFullScreen() {
//        if (isFullscreen) {
//            // Switch to windowed mode
//            frame.dispose(); // Dispose of the current frame
//            frame.setUndecorated(false); // Remove fullscreen decorations
//            frame.setSize(800, 600); // Set your desired window size
//            frame.setLocationRelativeTo(null); // Center the window
//            frame.setVisible(true); // Show the window
//        } else {
//            // Switch to fullscreen mode
//            frame.dispose(); // Dispose of the current frame
//            frame.setUndecorated(true); // Remove window decorations
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the frame
//            frame.setVisible(true); // Show the frame
//        }
//        isFullscreen = !isFullscreen; // Toggle the state
//    }

    public void gameSet() throws IOException {
        objectPlacer.placeObjects();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public int getNextAvailableWeaponIndex() {
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] == null) {
                return i; // Return the first available index
            }
        }
        return -1; // Return -1 if no available index is found
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Layer 1: Draw the tiles
        tileManager.draw(g2);

        // Layer 2: Draw the weapons
        for (Weapon weapon : weapons) {
            if (weapon != null) {
                weapon.draw(g2, this);
            }
        }

        // Layer 3: Draw the player
        try {
            player.draw(g2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        drawBulletsLeft(g2, player.currentWeapon); // Pass the current weapon to get ammo count

        // Draw FPS
        drawFPS(g2);

        g2.dispose(); // Dispose of this graphics context and release any system resources
    }

    private void drawFPS(Graphics2D g2) {
        String fpsText = "FPS: " + currentFPS;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20)); // Adjust font size here
        g2.drawString(fpsText, 10, 20); // Draw FPS at the top left corner
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

    @Override
    public void run() {
        double interval = 1_000_000_000.0 / FPS;
        long lastTime = System.nanoTime();
        long now;

        while (gameThread != null) {
            now = System.nanoTime();
            double delta = (now - lastTime) / interval;

            if (delta >= 1) {
                try {
                    update();
                    repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastTime = now;
            }

            // Sleep the thread for a short duration
            try {
                Thread.sleep(1); // Prevent busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() throws IOException {
        player.update();

        // Check for bullet collisions with the player
        for (int i = Player.bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = Player.bullets.get(i);

            // Log bullet position and player's health for debugging
            System.out.printf("Bullet at position X: %d, Y: %d%n", bullet.x, bullet.y);
            System.out.printf("Player Health: %d%n", player.health);

            if (bullet.calculateRectangle().intersects(player.bounds)) {
                player.takeDamage(bullet.damage); // Apply damage to the player
                System.out.println("Player hit by bullet! Damage taken: " + bullet.damage);
                Player.bullets.remove(i); // Remove the bullet after it hits
            }
        }
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
