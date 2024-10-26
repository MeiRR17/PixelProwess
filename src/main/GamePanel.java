package main;

import entity.Player;
import object.ObjMaster;
import tile.TileManager;
import utility.KeyHandler;
import utility.MouseHandler;

import javax.swing.*;
import java.awt.*;
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
    public final int screenWidth = tileSize * maxScreenColumn;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int worldColumn = 80;
    public final int worldRow = 80;

    int FPS = 60;
    TileManager tileManager = new TileManager(this);

    KeyHandler keyHandler = new KeyHandler();
    private MouseHandler mouseHandler;
    Thread gameThread;
    public Collision collisionCheck = new Collision(this);
    public ObjectPlacer objectPlacer = new ObjectPlacer(this);
    public Player player = new Player(this, keyHandler, null);
    public ObjMaster[] objMaster = new ObjMaster[25];


    public GamePanel() throws IOException {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the wanted size of the panel
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Set this component to be double buffered
        this.addKeyListener(keyHandler);
        this.setFocusable(true); // Make the GamePanel receive key input
        mouseHandler = new MouseHandler(player);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(this); // Add the mouse motion listener
        player = new Player(this, keyHandler, mouseHandler); // Updated line
    }

    public void gameSet() throws IOException {
        objectPlacer.placeObjects();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
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
        // Update player state
    }

    // To draw something on the screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Layer 1
        tileManager.draw(g2);
        // Layer 2
        for (ObjMaster master : objMaster) {
            if (master != null) {
                master.draw(g2, this);
            }
        }
        // Layer 3
        try {
            player.draw(g2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        g2.dispose(); // Dispose of this graphics context and release any system resources
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
