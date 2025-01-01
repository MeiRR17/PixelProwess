package main;

import entity.Player;
import entity.ui.*;
import object.bullets.Bullet;
import object.chests.Chest;
import object.weapons.Weapon;
import tile.TileManager;
import utility.KeyHandler;
import utility.MouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;


public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int screenWidth = screenSize.width;
    public final int screenHeight = screenSize.height - 30;
    public ArrayList<BaseMob> mobs = new ArrayList<>(); // Update to BaseMob
    public boolean debug = false; // Add debug mode flag

    // Screen settings
    public final int orgTileSize = 32; // 32x32 tile
    final int scale = 2; // Scaling the tile to make it bigger

    public final int tileSize = orgTileSize * scale;
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 18;

    public final int worldColumn = 100;
    public final int worldRow = 100;

    int FPS = 60;

    public KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    public Collision collisionCheck = new Collision(this);
    public ObjectPlacer objectPlacer = new ObjectPlacer(this);
    public Player player = new Player(this, keyHandler, null);
    public Weapon[] weapons = new Weapon[25];
    public TileManager tileManager;

    public GamePanel() throws IOException {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the wanted size of the panel
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Set this component to be double buffered
        this.addKeyListener(keyHandler);
        this.setFocusable(true); // Make the GamePanel receive key input
        MouseHandler mouseHandler = new MouseHandler(player);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(this); // Add the mouse motion listener
        player = new Player(this, keyHandler, mouseHandler); // Updated line
        tileManager = new TileManager(this);

        // Initialize the chest list in objectPlacer
        objectPlacer.chests = new ArrayList<>();
    }


    public void gameSet() throws IOException {
        objectPlacer.placeObjects(); // Ensure objects are placed in objectPlacer.chests
        // Update mob creation to match constructor
        for (int i = 0; i < 2; i++) {
            mobs.add(new Goblin(this, player, tileManager));
            mobs.add(new Orc(this, player, tileManager));
            mobs.add(new Uruk(this, player, tileManager));
            mobs.add(new Satan(this, player, tileManager));
        }
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

        tileManager.draw(g2);

        for (Weapon weapon : weapons) {
            if (weapon != null) {
                weapon.draw(g2, this);
            }
        }

        for (BaseMob mob : mobs) {
            mob.draw(g2);
        }

        // Drawing chests from objectPlacer.chests
        for (Chest chest : objectPlacer.chests) {
            chest.draw(g2);
        }

        player.draw(g2);
        drawBulletsLeft(g2, player.currentWeapon);

        g2.dispose();
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
        double interval = (double) 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + interval;

        while (gameThread != null) {
            // UPDATE : update information
            try {
                update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // DRAW : draw the screen (basically the FPS of the game)
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += interval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() throws IOException {
        player.update();

        // Update and check bullets
        for (int i = Player.bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = Player.bullets.get(i);
            Rectangle bulletBox = new Rectangle(
                    bullet.x,
                    bullet.y,
                    bullet.width,
                    bullet.height
            );

            // Check mob collisions
            for (int j = mobs.size() - 1; j >= 0; j--) {
                BaseMob mob = mobs.get(j);
                Rectangle mobBox = mob.getWorldCollisionBox();

                if (bulletBox.intersects(mobBox)) {
                    mob.takeDamage(bullet.damage);
                    Player.bullets.remove(i);
                    break;
                }
            }
        }

        // Check if player is close to any chest and press E to open it
        for (Chest chest : objectPlacer.chests) {
            // Check if the chest is within range and the "E" key is pressed
            if (chest.isWithinRange(player.playerX, player.playerY, 32) && keyHandler.isEPressed) {
                chest.open();  // Open the chest if conditions are met
            }
        }

        // Update mobs and remove dead ones
        for (int i = mobs.size() - 1; i >= 0; i--) {
            BaseMob mob = mobs.get(i);
            mob.update();
            if (mob.getHealth() <= 0) {
                mobs.remove(i);
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
