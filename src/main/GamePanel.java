package main;

import entity.AIMob;
import entity.Player;
import object.bullets.Bullet;
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


public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public final int screenWidth = screenSize.width;
    public final int screenHeight = screenSize.height - 30;
    public ArrayList<AIMob> mobs = new ArrayList<>();

    // Screen settings
    public final int orgTileSize = 32; // 32x32 tile
    final int scale = 2; // Scaling the tile to make it bigger

    public final int tileSize = orgTileSize * scale;
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 18;

    public final int worldColumn = 100;
    public final int worldRow = 100;
//    private int something = Toolkit.getDefaultToolkit().getScreenResolution();

    int FPS = 60;

    KeyHandler keyHandler = new KeyHandler();
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
//        System.out.println(something);
    }

    public void gameSet() throws IOException {
        objectPlacer.placeObjects();
        // Add multiple mobs to the list
        for (int i = 0; i < 5; i++) {
            mobs.add(new AIMob(this, player, tileManager)); // Pass the tileManager instance
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

        // Layer 1: Draw the tiles
        tileManager.draw(g2);

        // Layer 2: Draw the weapons
        for (Weapon weapon : weapons) {
            if (weapon != null) {
                weapon.draw(g2, this);
            }
        }

        for (AIMob mob : mobs) {
            mob.draw(g2);
        }

        // Layer 3: Draw the player
        player.draw(g2); // Removed try-catch block

        drawBulletsLeft(g2, player.currentWeapon); // Pass the current weapon to get ammo count

        g2.dispose(); // Dispose of this graphics context and release any system resources
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

            // Create bullet's world collision box
            Rectangle bulletBox = new Rectangle(
                    bullet.x,  // Remove the player position offset
                    bullet.y,  // Remove the player position offset
                    bullet.width,
                    bullet.height
            );

            // Check mob collisions
            for (int j = mobs.size() - 1; j >= 0; j--) {
                AIMob mob = mobs.get(j);
                Rectangle mobBox = mob.getWorldCollisionBox();

                // Create mob's actual world position box
                if (bulletBox.intersects(mobBox)) {
                    // Apply damage from bullet to mob
                    mob.takeDamage(bullet.damage);
                    System.out.println("Mob hit! Health: " + mob.getHealth()); // Debug line
                    Player.bullets.remove(i);
                    break;  // Break since bullet is removed
                }
            }
        }

        // Update mobs and remove dead ones
        for (int i = mobs.size() - 1; i >= 0; i--) {
            AIMob mob = mobs.get(i);
            mob.update();
            if (mob.getHealth() <= 0) {
                mobs.remove(i);
                System.out.println("Mob eliminated!"); // Debug line
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